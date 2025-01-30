package br.edu.ifpb.pps.projeto.modumender.dao;

import br.edu.ifpb.pps.projeto.modumender.Column;
import br.edu.ifpb.pps.projeto.modumender.Entity;
import br.edu.ifpb.pps.projeto.modumender.Id;
import br.edu.ifpb.pps.projeto.modumender.util.ConexaoDB;
import br.edu.ifpb.pps.projeto.modumender.SchemaGenerator;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GenericDAO<T> {
    private final Class<T> clazz;

    public GenericDAO(Class<T> clazz) {
        this.clazz = clazz;
        validateEntity();
    }

    private void validateEntity() {
        if (!clazz.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("A classe " + clazz.getName() + " não está anotada com @Entity.");
        }

        Field[] fields = clazz.getDeclaredFields();
        boolean hasPrimaryKey = false;

        for (Field field : fields) {
            if (field.isAnnotationPresent(Id.class)) {
                hasPrimaryKey = true;
                break;
            }
        }

        if (!hasPrimaryKey) {
            throw new IllegalArgumentException("A classe " + clazz.getName() + " não possui um campo anotado com @Id.");
        }
    }

    public void save(T entity) throws SQLException {
        String sql = SchemaGenerator.generateInsert(entity);
        try (Connection conn = ConexaoDB.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            int index = 1;
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Column.class) || field.isAnnotationPresent(Id.class)) {
                    field.setAccessible(true);
                    try {
                        stmt.setObject(index++, field.get(entity));
                    } catch (IllegalAccessException e) {
                        throw new SQLException("Erro ao acessar o campo: " + field.getName(), e);
                    }
                }
            }
            stmt.executeUpdate();
        }
    }

    public T findById(int id) throws SQLException {
        String sql = SchemaGenerator.generateFindById(clazz);

        try (Connection conn = ConexaoDB.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEntity(rs);
                }
            }
        }
        return null;
    }

    public List<T> findAll() throws SQLException {
        String sql = SchemaGenerator.generateFindAll(clazz);
        List<T> results = new ArrayList<>();

        try (Connection conn = ConexaoDB.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                results.add(mapResultSetToEntity(rs));
            }
        }
        return results;
    }

    public List<T> findWithFilters(Map<String, Object> filters, String orderBy, boolean ascending) throws SQLException {
        String tableName = clazz.getAnnotation(Entity.class).tableName();
        if (tableName.isEmpty()) {
            tableName = clazz.getSimpleName().toLowerCase();
        }

        StringBuilder query = new StringBuilder("SELECT * FROM ").append(tableName);

        if (filters != null && !filters.isEmpty()) {
            query.append(" WHERE ");
            filters.forEach((key, value) -> query.append(key).append(" = ? AND "));
            query.setLength(query.length() - 4); // Remove o último AND
        }

        if (orderBy != null && !orderBy.isBlank()) {
            query.append(" ORDER BY ").append(orderBy).append(ascending ? " ASC" : " DESC");
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
                    results.add(mapResultSetToEntity(rs));
                }
            }
        }
        return results;
    }

    private T mapResultSetToEntity(ResultSet rs) throws SQLException {
        try {
            T entity = clazz.getDeclaredConstructor().newInstance();
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                Object value = rs.getObject(field.getName());
                field.set(entity, value);
            }
            return entity;
        } catch (Exception e) {
            throw new SQLException("Erro ao mapear ResultSet para entidade", e);
        }
    }
}
