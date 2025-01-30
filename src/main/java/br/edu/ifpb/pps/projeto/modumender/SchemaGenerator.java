package br.edu.ifpb.pps.projeto.modumender;

import br.edu.ifpb.pps.projeto.modumender.util.ConexaoDB;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.reflections.Reflections;
import java.util.Set;


public class SchemaGenerator {

    public static void generateAllSchemas() {
        System.out.println("\uD83D\uDD39 Iniciando geração do banco de dados...");

        // Buscar todas as classes anotadas com @Entity
        Reflections reflections = new Reflections("br.edu.ifpb.pps.projeto.modumender.model");
        Set<Class<?>> entityClasses = reflections.getTypesAnnotatedWith(Entity.class);

        // Gerar tabelas para todas as classes encontradas
        for (Class<?> entityClass : entityClasses) {
            System.out.println("\uD83D\uDCCC Criando tabela para: " + entityClass.getSimpleName());
            validateEntity(entityClass);
            generateSchema(entityClass);
        }

        System.out.println("✅ Todas as tabelas foram geradas com sucesso!");
    }

    public static void generateSchema(Class<?> clazz) {
        // Validar a entidade antes de criar a tabela
        validateEntity(clazz);


        if (!clazz.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("Classe não é uma entidade: " + clazz.getName());
        }

        String tableName = clazz.getAnnotation(Entity.class).tableName();
        if (tableName.isEmpty()) {
            tableName = clazz.getSimpleName().toLowerCase();
        }

        StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        sql.append(tableName).append(" (");

        Field[] fields = clazz.getDeclaredFields();
        boolean hasPrimaryKey = false;

        for (Field field : fields) {
            if (!field.isAnnotationPresent(Column.class) && !field.isAnnotationPresent(Id.class)) {
                continue;
            }

            if (field.isAnnotationPresent(Id.class)) {
                if (hasPrimaryKey) {
                    throw new IllegalArgumentException("Mais de uma chave primária definida em: " + clazz.getName());
                }
                sql.append(field.getName()).append(" SERIAL PRIMARY KEY, ");
                hasPrimaryKey = true;
                continue;
            }

            if (field.isAnnotationPresent(Column.class)) {
                Column column = field.getAnnotation(Column.class);
                sql.append(field.getName()).append(" ");

                String sqlType = getSqlType(field.getType());
                if (sqlType == null) {
                    throw new IllegalArgumentException("Tipo de dado não suportado: " + field.getType().getSimpleName() +
                            " em " + clazz.getName() + "." + field.getName());
                }

                sql.append(sqlType);
                if (!column.nullable()) {
                    sql.append(" NOT NULL");
                }
                sql.append(", ");
            }
        }

        if (!hasPrimaryKey) {
            throw new IllegalArgumentException("Nenhuma chave primária definida em: " + clazz.getName());
        }

        sql.setLength(sql.length() - 2);
        sql.append(");");

        try (Connection conn = ConexaoDB.getInstance();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql.toString());
            System.out.println("✅ Tabela criada: " + tableName);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar tabela: " + tableName, e);
        }
    }

    public static void validateEntity(Class<?> clazz) {
        if (!clazz.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("Classe " + clazz.getName() + " não possui a anotação @Entity.");
        }

        Field[] fields = clazz.getDeclaredFields();
        boolean hasPrimaryKey = false;

        for (Field field : fields) {
            if (field.isAnnotationPresent(Id.class)) {
                hasPrimaryKey = true;
            }

            if (field.isAnnotationPresent(Column.class) || field.isAnnotationPresent(Id.class)) {
                if (getSqlType(field.getType()) == null) {
                    throw new IllegalArgumentException("Tipo de dado não suportado para o campo: "
                            + field.getName() + " em " + clazz.getName());
                }
            }

            if (field.isAnnotationPresent(ManyToOne.class)) {
                if (!field.getType().isAnnotationPresent(Entity.class)) {
                    throw new IllegalArgumentException("Relacionamento @ManyToOne inválido! O campo "
                            + field.getName() + " deve referenciar uma entidade válida.");
                }
            }

            if (field.isAnnotationPresent(ManyToMany.class)) {
                if (!List.class.isAssignableFrom(field.getType())) {
                    throw new IllegalArgumentException("Relacionamento @ManyToMany inválido! O campo "
                            + field.getName() + " deve ser uma lista de entidades.");
                }
            }
        }

        if (!hasPrimaryKey) {
            throw new IllegalArgumentException("A entidade " + clazz.getName() + " não possui um campo @Id.");
        }
    }


    private static String getSqlType(Class<?> javaType) {
        if (javaType == String.class) return "VARCHAR(255)";
        if (javaType == int.class || javaType == Integer.class) return "INT";
        if (javaType == double.class || javaType == Double.class) return "DOUBLE";
        if (javaType == boolean.class || javaType == Boolean.class) return "BOOLEAN";
        if (javaType == java.util.Date.class || javaType == java.sql.Date.class) return "DATE";
        return "TEXT"; // Tipo genérico
    }


    public static String generateFindAll(Class<?> clazz) {
        String tableName = clazz.getSimpleName().toLowerCase();
        return String.format("SELECT * FROM %s", tableName);
    }

    public static String generateFindById(Class<?> clazz) {
        String tableName = clazz.getSimpleName().toLowerCase();
        return String.format("SELECT * FROM %s WHERE id = ?", tableName);
    }


    public static <T> String generateInsert(T entity) {
        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();
        String tableName = entity.getClass().getSimpleName().toLowerCase();

        for (Field field : entity.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Column.class) || field.isAnnotationPresent(Id.class)) {
                field.setAccessible(true);
                columns.append(field.getName()).append(",");
                values.append("?").append(",");
            }
        }

        columns.setLength(columns.length() - 1); // Remove última vírgula
        values.setLength(values.length() - 1);
        return String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, columns, values);
    }
}
