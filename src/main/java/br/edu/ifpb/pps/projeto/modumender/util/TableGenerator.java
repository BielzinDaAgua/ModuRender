package br.edu.ifpb.pps.projeto.modumender.util;

import br.edu.ifpb.pps.projeto.modumender.annotations.Column;
import br.edu.ifpb.pps.projeto.modumender.annotations.Entity;
import br.edu.ifpb.pps.projeto.modumender.annotations.Id;

import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * Responsável por criar as tabelas das entidades (colunas + PK).
 */
public class TableGenerator {

    /**
     * Cria as tabelas (para cada classe) usando reflection.
     */
    public static void generateTables(Set<Class<?>> entityClasses) {
        System.out.println("🔸 Iniciando criação das tabelas...");
        for (Class<?> entityClass : entityClasses) {
            createTableForEntity(entityClass);
        }
        System.out.println("✅ Tabelas criadas com sucesso.");
    }

    private static void createTableForEntity(Class<?> clazz) {
        // Só processa se tiver @Entity
        if (!clazz.isAnnotationPresent(Entity.class)) {
            return;
        }

        String tableName = SQLUtils.getTableName(clazz);
        StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (");

        Field[] fields = clazz.getDeclaredFields();
        boolean hasPrimaryKey = false;

        for (Field field : fields) {
            Id idAnn = field.getAnnotation(Id.class);
            Column colAnn = field.getAnnotation(Column.class);

            // Se não for @Id ou @Column, pula
            if (idAnn == null && colAnn == null) {
                continue;
            }

            // Descobre o nome da coluna
            String columnName = SQLUtils.getColumnName(field);

            if (idAnn != null) {
                // PK => assumimos SERIAL
                if (hasPrimaryKey) {
                    throw new IllegalArgumentException(
                            "Mais de um @Id encontrado em " + clazz.getName()
                    );
                }
                hasPrimaryKey = true;
                sql.append(columnName).append(" SERIAL PRIMARY KEY, ");
            } else {
                // Campo normal
                String sqlType = SQLUtils.getSqlType(field.getType());
                if (sqlType == null) {
                    throw new IllegalArgumentException(
                            "Tipo não suportado: " + field.getType().getSimpleName()
                                    + " em " + clazz.getName() + "." + field.getName()
                    );
                }
                sql.append(columnName).append(" ").append(sqlType);

                // Tratando se pode ser null ou não
                if (!colAnn.nullable()) {
                    sql.append(" NOT NULL");
                }
                sql.append(", ");
            }
        }

        if (!hasPrimaryKey) {
            throw new IllegalArgumentException(
                    "Nenhum campo @Id encontrado em " + clazz.getName()
            );
        }

        // Remove a última vírgula
        SQLUtils.removeTrailingComma(sql);
        sql.append(");");

        SQLUtils.executeSQL(sql.toString(), "Criar Tabela: " + tableName);
    }
}
