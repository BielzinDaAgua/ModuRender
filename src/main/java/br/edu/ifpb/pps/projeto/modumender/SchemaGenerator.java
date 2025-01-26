package br.edu.ifpb.pps.projeto.modumender;

import java.lang.reflect.Field;
import java.util.List;

public class SchemaGenerator {

    public static void generateSchema(Class<?> clazz) {
        if (!clazz.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("Classe não é uma entidade: " + clazz.getName());
        }

        StringBuilder sql = new StringBuilder("CREATE TABLE ");
        sql.append(clazz.getSimpleName().toLowerCase()).append(" (");

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Id.class)) {
                sql.append(field.getName()).append(" SERIAL PRIMARY KEY, ");
            } else if (field.isAnnotationPresent(Column.class)) {
                sql.append(field.getName()).append(" ");
                sql.append(getSqlType(field.getType()));
                if (field.getAnnotation(Column.class).nullable() == false) {
                    sql.append(" NOT NULL");
                }
                sql.append(", ");
            }
        }
        sql.setLength(sql.length() - 2); // Remove a última vírgula
        sql.append(");");

        System.out.println(sql.toString());
        // Opcional: Execute diretamente no banco.
    }

    private static String getSqlType(Class<?> javaType) {
        if (javaType == String.class) return "VARCHAR(255)";
        if (javaType == int.class || javaType == Integer.class) return "INT";
        if (javaType == double.class || javaType == Double.class) return "DOUBLE";
        if (javaType == boolean.class || javaType == Boolean.class) return "BOOLEAN";
        if (javaType == java.util.Date.class || javaType == java.sql.Date.class) return "DATE";
        return "TEXT"; // Tipo genérico
    }

    public <T> List<T> generateFindAll(Class<T> clazz) {
        return List.of();//criar
    }

    public <T> T generateFindById(Class<T> clazz, int id) {
        return null;//criar
    }

    public <T> void generateInsert(T entity) {
    }//criar
}
