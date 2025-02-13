package br.edu.ifpb.pps.projeto.modumender.util;

import br.edu.ifpb.pps.projeto.modumender.annotations.Column;
import br.edu.ifpb.pps.projeto.modumender.annotations.Entity;
import br.edu.ifpb.pps.projeto.modumender.util.ConexaoDB;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLUtils {

    public static String getTableName(Class<?> clazz) {
        Entity ann = clazz.getAnnotation(Entity.class);
        String tableName = ann.tableName();
        return tableName.isEmpty() ? clazz.getSimpleName().toLowerCase() : tableName;
    }

    public static String getColumnName(Field field) {
        Column colAnn = field.getAnnotation(Column.class);
        return (colAnn != null && !colAnn.name().isEmpty()) ? colAnn.name() : field.getName();
    }

    public static String getSqlType(Class<?> javaType) {
        if (javaType == String.class) return "VARCHAR(255)";
        if (javaType == int.class || javaType == Integer.class) return "INT";
        if (javaType == double.class || javaType == Double.class) return "DOUBLE PRECISION";
        if (javaType == boolean.class || javaType == Boolean.class) return "BOOLEAN";
        if (javaType == java.util.Date.class || javaType == java.sql.Date.class) return "DATE";
        return null;
    }

    public static void executeSQL(String sql, String info) {
        try (Connection conn = ConexaoDB.getInstance();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Erro ao executar SQL (" + info + "): " + sql);
            System.err.println("Motivo: " + e.getMessage());
        }
    }

    public static void executeSQLSafeConstraint(String sql) {
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

    public static void removeTrailingComma(StringBuilder sql) {
        if (sql.lastIndexOf(", ") == sql.length() - 2) {
            sql.delete(sql.length() - 2, sql.length());
        }
    }
}
