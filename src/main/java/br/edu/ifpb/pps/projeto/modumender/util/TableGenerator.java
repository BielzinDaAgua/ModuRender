package br.edu.ifpb.pps.projeto.modumender.util;

import br.edu.ifpb.pps.projeto.modumender.annotations.*;
import br.edu.ifpb.pps.projeto.modumender.util.SQLUtils;
import org.reflections.Reflections;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Set;

public class TableGenerator {

    public static void generateTables() {
        System.out.println("🔸 Iniciando geração das tabelas...");
        Reflections reflections = new Reflections("br.edu.ifpb.pps.projeto.modumender.model");
        Set<Class<?>> entityClasses = reflections.getTypesAnnotatedWith(Entity.class);

        for (Class<?> entityClass : entityClasses) {
            createTableForEntity(entityClass);
        }
        System.out.println("✅ Tabelas criadas com sucesso.");
    }

    private static void createTableForEntity(Class<?> clazz) {
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

            if (idAnn == null && colAnn == null) {
                continue;
            }

            String columnName = SQLUtils.getColumnName(field);
            if (idAnn != null) {
                if (hasPrimaryKey) {
                    throw new IllegalArgumentException("Mais de um @Id na classe: " + clazz.getName());
                }
                hasPrimaryKey = true;
                sql.append(columnName).append(" SERIAL PRIMARY KEY, ");
            } else {
                String sqlType = SQLUtils.getSqlType(field.getType());
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

        SQLUtils.removeTrailingComma(sql);
        sql.append(");");
        SQLUtils.executeSQL(sql.toString(), "Criar Tabela: " + tableName);
    }
}
