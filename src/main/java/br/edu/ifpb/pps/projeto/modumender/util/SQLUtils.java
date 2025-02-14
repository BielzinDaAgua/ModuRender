package br.edu.ifpb.pps.projeto.modumender.util;

import br.edu.ifpb.pps.projeto.modumender.annotations.Column;
import br.edu.ifpb.pps.projeto.modumender.annotations.Entity;
import br.edu.ifpb.pps.projeto.modumender.annotations.Id;

import br.edu.ifpb.pps.projeto.modumender.util.ConexaoDB;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Métodos auxiliares para gerar e executar SQL,
 * descobrir nomes de tabela e coluna, etc.
 */
public class SQLUtils {

    /**
     * Retorna o nome da tabela a partir de @Entity(tableName).
     * Se tableName = "", usa o nome da classe em minúsculo.
     */
    public static String getTableName(Class<?> clazz) {
        Entity ann = clazz.getAnnotation(Entity.class);
        if (ann == null) {
            throw new IllegalArgumentException("Classe não possui @Entity: " + clazz.getName());
        }
        String tableName = ann.tableName();
        return (tableName.isEmpty()) ? clazz.getSimpleName().toLowerCase() : tableName;
    }

    /**
     * Retorna o nome da coluna, com base em @Column(name)
     * ou, se não houver name, usa o nome do field.
     */
    public static String getColumnName(Field field) {
        Column colAnn = field.getAnnotation(Column.class);
        if (colAnn != null && !colAnn.name().isEmpty()) {
            return colAnn.name();
        }
        return field.getName();
    }

    /**
     * Converte o tipo Java para SQL (básico).
     */
    public static String getSqlType(Class<?> javaType) {
        if (javaType == String.class)                   return "VARCHAR(255)";
        if (javaType == int.class || javaType == Integer.class)
            return "INT";
        if (javaType == double.class || javaType == Double.class)
            return "DOUBLE PRECISION";
        if (javaType == boolean.class || javaType == Boolean.class)
            return "BOOLEAN";
        if (javaType == java.util.Date.class || javaType == java.sql.Date.class)
            return "DATE";
        // Extender se precisar para float, long, BigDecimal, etc.
        return null;
    }

    /**
     * Executa uma instrução SQL (CREATE, ALTER, etc.).
     */
    public static void executeSQL(String sql, String info) {
        try (Connection conn = ConexaoDB.getInstance();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            // System.out.println("Executado: " + info + " -> " + sql);
        } catch (SQLException e) {
            System.err.println("Erro ao executar SQL (" + info + "): " + sql);
            System.err.println("Motivo: " + e.getMessage());
        }
    }

    /**
     * Executa SQL para constraint e ignora se "already exists".
     */
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

    /**
     * Remove a última ", " (vírgula + espaço) de um StringBuilder,
     * caso exista. Usado na hora de montar colunas.
     */
    public static void removeTrailingComma(StringBuilder sql) {
        if (sql.lastIndexOf(", ") == sql.length() - 2) {
            sql.delete(sql.length() - 2, sql.length());
        }
    }

    // -------------------------------------------------------
    // CRUD geradores
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
                // Nome da coluna
                String columnName = getColumnName(field);

                columns.append(columnName).append(", ");
                placeholders.append("?, ");
            }
        }
        // Remove a última vírgula e espaço
        removeTrailingComma(columns);
        removeTrailingComma(placeholders);

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
