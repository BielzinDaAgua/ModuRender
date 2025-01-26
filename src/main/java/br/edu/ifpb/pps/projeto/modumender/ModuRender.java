package br.edu.ifpb.pps.projeto.modumender;

import br.edu.ifpb.pps.projeto.modumender.services.*;
import br.edu.ifpb.pps.projeto.modumender.models.*;
import br.edu.ifpb.pps.projeto.modumender.util.ConexaoDB;
import br.edu.ifpb.pps.projeto.modumender.util.MigrationManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.lang.reflect.Field;


public class ModuRender {

    static {
        // Inicializar o framework: criar as tabelas automaticamente com base nas anotações
        MigrationManager.criarTabelas();
    }

    // Salvar uma entidade no banco
    public static <T> void save(T entity) throws SQLException {
        String tableName = entity.getClass().getSimpleName().toLowerCase();
        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();

        List<Object> params = new ArrayList<>();
        Field[] fields = entity.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(entity);
                if (value != null) {
                    columns.append(field.getName()).append(",");
                    values.append("?,");
                    params.add(value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        // Montar e executar a query SQL
        String sql = String.format("INSERT INTO %s (%s) VALUES (%s)", tableName,
                columns.substring(0, columns.length() - 1),
                values.substring(0, values.length() - 1));

        try (Connection conn = ConexaoDB.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            stmt.executeUpdate();
        }
    }

    // Buscar uma entidade pelo ID
    public static <T> T findById(Class<T> clazz, int id) throws SQLException {
        String tableName = clazz.getSimpleName().toLowerCase();
        String sql = String.format("SELECT * FROM %s WHERE id = ?", tableName);

        try (Connection conn = ConexaoDB.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs, clazz);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Listar todas as entidades de um tipo
    public static <T> List<T> findAll(Class<T> clazz) throws SQLException {
        String tableName = clazz.getSimpleName().toLowerCase();
        String sql = String.format("SELECT * FROM %s", tableName);

        List<T> results = new ArrayList<>();
        try (Connection conn = ConexaoDB.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                results.add(mapResultSetToEntity(rs, clazz));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    // Buscar entidades com filtros, ordenação e joins
    public static <T> List<T> findWithFilters(Class<T> entityClass,
                                              Map<String, Object> filters,
                                              String orderBy,
                                              boolean ascending,
                                              String joinClause) throws SQLException {

        String tableName = entityClass.getSimpleName().toLowerCase();
        StringBuilder query = new StringBuilder("SELECT * FROM " + tableName);

        // Adicionar cláusulas JOIN
        if (joinClause != null && !joinClause.isBlank()) {
            query.append(" ").append(joinClause);
        }

        // Adicionar cláusula WHERE
        if (filters != null && !filters.isEmpty()) {
            query.append(" WHERE ");
            filters.forEach((key, value) -> query.append(key).append(" = ? AND "));
            query.setLength(query.length() - 4); // Remover o último "AND"
        }

        // Adicionar cláusula ORDER BY
        if (orderBy != null && !orderBy.isBlank()) {
            query.append(" ORDER BY ").append(orderBy);
            query.append(ascending ? " ASC" : " DESC");
        }

        List<T> results = new ArrayList<>();
        try (Connection conn = ConexaoDB.getInstance();
             PreparedStatement stmt = conn.prepareStatement(query.toString())) {

            if (filters != null) {
                int index = 1;
                for (Object value : filters.values()) {
                    stmt.setObject(index++, value);
                }
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapResultSetToEntity(rs, entityClass));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    // Utilitário para mapear ResultSet para entidade
    private static <T> T mapResultSetToEntity(ResultSet rs, Class<T> clazz) throws Exception {
        T entity = clazz.getDeclaredConstructor().newInstance();
        ResultSetMetaData metaData = rs.getMetaData();

        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            String columnName = metaData.getColumnName(i);
            Object columnValue = rs.getObject(i);

            try {
                Field field = clazz.getDeclaredField(columnName);
                field.setAccessible(true);
                field.set(entity, columnValue);
            } catch (NoSuchFieldException e) {
                // Ignorar campos não mapeados
            }
        }
        return entity;
    }
}
