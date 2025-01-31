package br.edu.ifpb.pps.projeto.modumender;

import br.edu.ifpb.pps.projeto.modumender.annotations.*;
import br.edu.ifpb.pps.projeto.modumender.util.ConexaoDB;

import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Set;

/**
 * Responsável por gerar/atualizar o schema (tabelas, FKs, etc.)
 * ao iniciar o framework.
 */
public class SchemaGenerator {

    public static void generateAllSchemas() {
        System.out.println("🔸 Iniciando geração das tabelas/relacionamentos...");

        // Busca todas as classes anotadas com @Entity no pacote model
        Reflections reflections = new Reflections("br.edu.ifpb.pps.projeto.modumender.model");
        Set<Class<?>> entityClasses = reflections.getTypesAnnotatedWith(Entity.class);

        // 1) Criar tabelas (colunas + PK)
        for (Class<?> entityClass : entityClasses) {
            createTableForEntity(entityClass);
        }

        // 2) Criar relacionamentos (FKs para ManyToOne, OneToOne e tabelas de junção para ManyToMany)
        for (Class<?> entityClass : entityClasses) {
            createRelationships(entityClass, entityClasses);
        }

        System.out.println("✅ Geração de schema concluída.\n");
    }

    private static void createTableForEntity(Class<?> clazz) {
        if (!clazz.isAnnotationPresent(Entity.class)) {
            return;
        }

        String tableName = getTableName(clazz);

        StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        sql.append(tableName).append(" (");

        Field[] fields = clazz.getDeclaredFields();
        boolean hasPrimaryKey = false;

        for (Field field : fields) {
            Id idAnn = field.getAnnotation(Id.class);
            Column colAnn = field.getAnnotation(Column.class);

            if (idAnn == null && colAnn == null) {
                continue; // não é campo persistente
            }

            // Nome da coluna
            String columnName = null;
            if (colAnn != null && !colAnn.name().isEmpty()) {
                columnName = colAnn.name();
            } else {
                columnName = field.getName();
            }

            if (idAnn != null) {
                // Se for PK, assumimos SERIAL
                if (hasPrimaryKey) {
                    throw new IllegalArgumentException("Mais de uma @Id na classe: " + clazz.getName());
                }
                hasPrimaryKey = true;
                sql.append(columnName).append(" SERIAL PRIMARY KEY, ");
            } else {
                // Normal @Column
                String sqlType = getSqlType(field.getType());
                if (sqlType == null) {
                    throw new IllegalArgumentException("Tipo não suportado: " + field.getType().getSimpleName());
                }
                sql.append(columnName).append(" ").append(sqlType);

                if (!colAnn.nullable()) {
                    sql.append(" NOT NULL");
                }

                sql.append(", ");
            }
        }

        if (!hasPrimaryKey) {
            throw new IllegalArgumentException("Nenhum campo @Id encontrado em " + clazz.getName());
        }

        // Remove última vírgula
        if (sql.lastIndexOf(", ") == sql.length() - 2) {
            sql.delete(sql.length() - 2, sql.length());
        }

        sql.append(");");

        executeSQL(sql.toString(), "Criar Tabela: " + tableName);
    }

    private static void createRelationships(Class<?> clazz, Set<Class<?>> allEntities) {
        String thisTable = getTableName(clazz);
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            // ### ManyToOne ###
            if (field.isAnnotationPresent(ManyToOne.class)) {
                ManyToOne manyToOne = field.getAnnotation(ManyToOne.class);
                Class<?> otherEntity = field.getType();
                if (!otherEntity.isAnnotationPresent(Entity.class)) {
                    throw new IllegalArgumentException("Relacionamento @ManyToOne inválido em " + field.getName());
                }
                String otherTable = getTableName(otherEntity);

                // Nome da coluna que guardará a FK
                String fkColumnName = field.getName() + "_id";
                String refCol = manyToOne.referencedColumnName(); // normalmente "id"

                // 1) Adiciona a coluna (se não existir)
                String sqlAddCol = String.format(
                        "ALTER TABLE %s ADD COLUMN IF NOT EXISTS %s INT",
                        thisTable, fkColumnName
                );
                executeSQL(sqlAddCol, "Add column para ManyToOne");

                // 2) Adiciona constraint FOREIGN KEY
                String sqlAddFK = String.format(
                        "ALTER TABLE %s ADD CONSTRAINT fk_%s_%s FOREIGN KEY (%s) REFERENCES %s(%s)",
                        thisTable, thisTable, fkColumnName, fkColumnName, otherTable, refCol
                );
                executeSQLSafeConstraint(sqlAddFK);
            }

            // ### OneToOne ###
            if (field.isAnnotationPresent(OneToOne.class)) {
                OneToOne oneToOne = field.getAnnotation(OneToOne.class);
                Class<?> otherEntity = field.getType();
                if (!otherEntity.isAnnotationPresent(Entity.class)) {
                    throw new IllegalArgumentException("Relacionamento @OneToOne inválido: " + field.getName());
                }
                String otherTable = getTableName(otherEntity);
                String fkColumnName = field.getName() + "_id";
                String refCol = oneToOne.referencedColumnName();

                String sqlAddCol = String.format(
                        "ALTER TABLE %s ADD COLUMN IF NOT EXISTS %s INT",
                        thisTable, fkColumnName
                );
                executeSQL(sqlAddCol, "Add column para OneToOne");

                String sqlAddFK = String.format(
                        "ALTER TABLE %s ADD CONSTRAINT fk_%s_%s FOREIGN KEY (%s) REFERENCES %s(%s)",
                        thisTable, thisTable, fkColumnName, fkColumnName, otherTable, refCol
                );
                executeSQLSafeConstraint(sqlAddFK);

                // Exemplo de unique constraint para 1:1
                String sqlAddUnique = String.format(
                        "ALTER TABLE %s ADD CONSTRAINT uk_%s_%s UNIQUE (%s)",
                        thisTable, thisTable, fkColumnName, fkColumnName
                );
                executeSQLSafeConstraint(sqlAddUnique);
            }

            // ### ManyToMany ###
            if (field.isAnnotationPresent(ManyToMany.class)) {
                ManyToMany manyToMany = field.getAnnotation(ManyToMany.class);

                // Nome da tabela de junção
                String joinTable = manyToMany.joinTable();
                String joinColumn = manyToMany.joinColumn();
                String inverseJoinColumn = manyToMany.inverseJoinColumn();

                // Tipo do field deve ser List<?>. Precisamos descobrir a classe do elemento
                Class<?> listType = getGenericListType(field);
                if (listType == null || !listType.isAnnotationPresent(Entity.class)) {
                    throw new IllegalArgumentException("Relacionamento @ManyToMany inválido em " + field.getName());
                }

                String otherTable = getTableName(listType);
                // Cria a tabela de junção
                String sqlCreateJoin = String.format(
                        "CREATE TABLE IF NOT EXISTS %s (%s INT NOT NULL, %s INT NOT NULL, PRIMARY KEY(%s, %s))",
                        joinTable, joinColumn, inverseJoinColumn, joinColumn, inverseJoinColumn
                );
                executeSQL(sqlCreateJoin, "Create joinTable para ManyToMany");

                // FK para esta entidade
                String sqlFK1 = String.format(
                        "ALTER TABLE %s ADD CONSTRAINT fk_%s_%s FOREIGN KEY (%s) REFERENCES %s(id)",
                        joinTable, joinTable, joinColumn, joinColumn, thisTable
                );
                executeSQLSafeConstraint(sqlFK1);

                // FK para a outra entidade
                String sqlFK2 = String.format(
                        "ALTER TABLE %s ADD CONSTRAINT fk_%s_%s FOREIGN KEY (%s) REFERENCES %s(id)",
                        joinTable, joinTable, inverseJoinColumn, inverseJoinColumn, otherTable
                );
                executeSQLSafeConstraint(sqlFK2);
            }
        }
    }

    // -------------------------------------------------------
    // Métodos auxiliares
    // -------------------------------------------------------

    private static String getTableName(Class<?> clazz) {
        Entity ann = clazz.getAnnotation(Entity.class);
        String tableName = ann.tableName();
        if (tableName.isEmpty()) {
            tableName = clazz.getSimpleName().toLowerCase();
        }
        return tableName;
    }

    private static String getSqlType(Class<?> javaType) {
        if (javaType == String.class) return "VARCHAR(255)";
        if (javaType == int.class || javaType == Integer.class) return "INT";
        if (javaType == double.class || javaType == Double.class) return "DOUBLE PRECISION";
        if (javaType == boolean.class || javaType == Boolean.class) return "BOOLEAN";
        if (javaType == java.util.Date.class || javaType == java.sql.Date.class) return "DATE";
        // etc. Adicione mais tipos se precisar
        return null;
    }

    private static void executeSQL(String sql, String info) {
        try (Connection conn = ConexaoDB.getInstance();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            // System.out.println(info + " executado com sucesso: " + sql);
        } catch (SQLException e) {
            System.err.println("Erro ao executar SQL (" + info + "): " + sql);
            System.err.println("Motivo: " + e.getMessage());
        }
    }

    private static void executeSQLSafeConstraint(String sql) {
        // Tenta criar constraint, se já existe, ignora.
        try (Connection conn = ConexaoDB.getInstance();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            if (!e.getMessage().contains("already exists")) {
                System.err.println("Erro ao executar constraint: " + sql);
                System.err.println("Motivo: " + e.getMessage());
            }
        }
    }

    /**
     * Verifica se o field é List<T> e retorna T (classe).
     */
    private static Class<?> getGenericListType(Field field) {
        try {
            if (!List.class.isAssignableFrom(field.getType())) {
                return null;
            }
            if (field.getGenericType() instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) field.getGenericType();
                if (pt.getActualTypeArguments().length == 1) {
                    return Class.forName(pt.getActualTypeArguments()[0].getTypeName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // -------------------------------------------------------
    // Geração de SQL para CRUD básico
    // -------------------------------------------------------
    public static String generateInsert(Object entity) {
        Class<?> clazz = entity.getClass();
        String tableName = getTableName(clazz);

        StringBuilder columns = new StringBuilder();
        StringBuilder placeholders = new StringBuilder();

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Id.class) || field.isAnnotationPresent(Column.class)) {
                // Se for @Id (SERIAL), normalmente o BD gera valor,
                // mas aqui, vamos inserir também, se o dev setar um valor.
                Column colAnn = field.getAnnotation(Column.class);
                Id idAnn = field.getAnnotation(Id.class);

                // Nome da coluna
                String columnName = (colAnn != null && !colAnn.name().isEmpty())
                        ? colAnn.name()
                        : field.getName();

                columns.append(columnName).append(", ");
                placeholders.append("?, ");
            }
        }
        // Remove última vírgula
        if (columns.length() > 2) {
            columns.setLength(columns.length() - 2);
            placeholders.setLength(placeholders.length() - 2);
        }

        String sql = String.format(
                "INSERT INTO %s (%s) VALUES (%s)",
                tableName, columns, placeholders
        );

        return sql;
    }

    public static String generateFindAll(Class<?> clazz) {
        String tableName = getTableName(clazz);
        return "SELECT * FROM " + tableName;
    }

    public static String generateFindById(Class<?> clazz) {
        String tableName = getTableName(clazz);
        return "SELECT * FROM " + tableName + " WHERE id = ?";
    }
}
