package br.edu.ifpb.pps.projeto.modumender.dao;

import br.edu.ifpb.pps.projeto.modumender.SchemaGenerator;
import br.edu.ifpb.pps.projeto.modumender.annotations.*;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * DAO genérico para operações de CRUD
 * em entidades anotadas com @Entity,
 * com validações extras (Min, Max, Length, etc.).
 */
public class GenericDAO<T> extends BaseDAO implements CrudRepository<T> {

    private final Class<T> clazz;

    public GenericDAO(Class<T> clazz) {
        this.clazz = clazz;
        validateEntity();
    }

    private void validateEntity() {
        if (!clazz.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException(
                    "A classe " + clazz.getName() + " não está anotada com @Entity."
            );
        }

        boolean hasPrimaryKey = false;
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                hasPrimaryKey = true;
                break;
            }
        }
        if (!hasPrimaryKey) {
            throw new IllegalArgumentException(
                    "A classe " + clazz.getName() + " não possui um campo anotado com @Id."
            );
        }
    }

    // =========================== CREATE (save) ===========================

    @Override
    public void save(T entity) throws SQLException {
        String sql = SchemaGenerator.generateInsert(entity);

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            Field[] fields = clazz.getDeclaredFields();
            int index = 1;

            for (Field field : fields) {
                if (field.isAnnotationPresent(Column.class) || field.isAnnotationPresent(Id.class)) {
                    field.setAccessible(true);

                    Object value;
                    try {
                        value = field.get(entity);
                    } catch (IllegalAccessException e) {
                        throw new SQLException("Erro ao acessar campo: " + field.getName(), e);
                    }

                    validateField(field, value);
                    stmt.setObject(index++, value);
                }
            }

            stmt.executeUpdate();
        }
    }

    private void validateField(Field field, Object value) {
        Column colAnn = field.getAnnotation(Column.class);
        if (colAnn != null && !colAnn.nullable() && value == null) {
            throw new IllegalArgumentException(
                    "O campo " + field.getName() + " não pode ser nulo!"
            );
        }

        // @Min
        if (field.isAnnotationPresent(Min.class)) {
            if (value == null) return;
            Min minAnn = field.getAnnotation(Min.class);
            int numericValue = convertToInt(value, field);
            if (numericValue < minAnn.value()) {
                throw new IllegalArgumentException("O campo " + field.getName() +
                        " deve ser >= " + minAnn.value() + ". Valor atual: " + numericValue);
            }
        }

        // @Max
        if (field.isAnnotationPresent(Max.class)) {
            if (value == null) return;
            Max maxAnn = field.getAnnotation(Max.class);
            int numericValue = convertToInt(value, field);
            if (numericValue > maxAnn.value()) {
                throw new IllegalArgumentException("O campo " + field.getName() +
                        " deve ser <= " + maxAnn.value() + ". Valor atual: " + numericValue);
            }
        }

        // @ExactDigits
        if (field.isAnnotationPresent(ExactDigits.class)) {
            if (value == null) return;
            ExactDigits edAnn = field.getAnnotation(ExactDigits.class);
            String strValue = value.toString();
            if (strValue.length() != edAnn.value()) {
                throw new IllegalArgumentException("O campo " + field.getName() +
                        " deve ter exatamente " + edAnn.value() + " dígitos/caracteres. Valor: " + strValue);
            }
        }

        // @Length
        if (field.isAnnotationPresent(Length.class)) {
            if (value == null) return;
            Length lenAnn = field.getAnnotation(Length.class);
            if (value instanceof String) {
                String strValue = (String) value;
                if (strValue.length() < lenAnn.min()) {
                    throw new IllegalArgumentException("O campo " + field.getName() +
                            " deve ter ao menos " + lenAnn.min() + " caracteres. Valor atual: " + strValue);
                }
                if (strValue.length() > lenAnn.max()) {
                    throw new IllegalArgumentException("O campo " + field.getName() +
                            " deve ter no máximo " + lenAnn.max() + " caracteres. Valor atual: " + strValue);
                }
            }
        }
    }

    private int convertToInt(Object value, Field field) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        throw new IllegalArgumentException(
                "O campo " + field.getName() + " não é numérico, mas tem @Min/@Max."
        );
    }

    // =========================== READ ===========================

    @Override
    public T findById(int id) throws SQLException {
        String sql = SchemaGenerator.generateFindById(clazz);
        try (Connection conn = getConnection();
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

    @Override
    public List<T> findAll() throws SQLException {
        String sql = SchemaGenerator.generateFindAll(clazz);
        List<T> results = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                results.add(mapResultSetToEntity(rs));
            }
        }
        return results;
    }

    public List<T> findWithFilters(Map<String, Object> filters, String orderBy, boolean ascending) throws SQLException {
        Entity entityAnn = clazz.getAnnotation(Entity.class);
        String tableName = entityAnn.tableName().isEmpty()
                ? clazz.getSimpleName().toLowerCase()
                : entityAnn.tableName();

        StringBuilder query = new StringBuilder("SELECT * FROM ").append(tableName);

        if (filters != null && !filters.isEmpty()) {
            query.append(" WHERE ");
            filters.forEach((key, value) -> {
                query.append(key).append(" = ? AND ");
            });
            query.setLength(query.length() - 5); // remove " AND "
        }

        if (orderBy != null && !orderBy.isBlank()) {
            query.append(" ORDER BY ")
                    .append(orderBy)
                    .append(ascending ? " ASC" : " DESC");
        }

        List<T> results = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query.toString())) {

            if (filters != null && !filters.isEmpty()) {
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

    public List<T> findByRange(String columnName, Object start, Object end) throws SQLException {
        Entity entityAnn = clazz.getAnnotation(Entity.class);
        String tableName = entityAnn.tableName().isEmpty()
                ? clazz.getSimpleName().toLowerCase()
                : entityAnn.tableName();

        String sql = "SELECT * FROM " + tableName + " WHERE " + columnName + " BETWEEN ? AND ?";
        List<T> results = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, start);
            stmt.setObject(2, end);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapResultSetToEntity(rs));
                }
            }
        }
        return results;
    }

    public List<T> findLike(String columnName, String pattern) throws SQLException {
        Entity entityAnn = clazz.getAnnotation(Entity.class);
        String tableName = entityAnn.tableName().isEmpty()
                ? clazz.getSimpleName().toLowerCase()
                : entityAnn.tableName();

        String sql = "SELECT * FROM " + tableName + " WHERE " + columnName + " LIKE ?";
        List<T> results = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, pattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapResultSetToEntity(rs));
                }
            }
        }
        return results;
    }

    public List<T> findByColumn(String columnName, Object value) throws SQLException {
        Entity entityAnn = clazz.getAnnotation(Entity.class);
        String tableName = entityAnn.tableName().isEmpty()
                ? clazz.getSimpleName().toLowerCase()
                : entityAnn.tableName();

        String sql = "SELECT * FROM " + tableName + " WHERE " + columnName + " = ?";
        List<T> results = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, value);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapResultSetToEntity(rs));
                }
            }
        }
        return results;
    }

    // =========================== DELETE ===========================

    @Override
    public void deleteById(int id) throws SQLException {
        Entity entityAnn = clazz.getAnnotation(Entity.class);
        String tableName = entityAnn.tableName().isEmpty()
                ? clazz.getSimpleName().toLowerCase()
                : entityAnn.tableName();

        String sql = "DELETE FROM " + tableName + " WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    // =========================== MAP ROW -> ENTITY ===========================

    private T mapResultSetToEntity(ResultSet rs) throws SQLException {
        try {
            T entity = clazz.getDeclaredConstructor().newInstance();
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);

                Column colAnn = field.getAnnotation(Column.class);
                Id idAnn = field.getAnnotation(Id.class);

                String columnName;
                if (colAnn != null && !colAnn.name().isEmpty()) {
                    columnName = colAnn.name();
                } else if (idAnn != null) {
                    columnName = field.getName();
                } else {
                    columnName = field.getName();
                }

                Object value = rs.getObject(columnName);
                field.set(entity, value);
            }
            return entity;
        } catch (Exception e) {
            throw new SQLException(
                    "Erro ao mapear ResultSet para a entidade: " + clazz.getName(), e
            );
        }
    }
}
