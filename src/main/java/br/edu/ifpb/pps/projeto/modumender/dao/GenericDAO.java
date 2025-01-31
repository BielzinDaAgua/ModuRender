package br.edu.ifpb.pps.projeto.modumender.dao;

import br.edu.ifpb.pps.projeto.modumender.annotations.Column;
import br.edu.ifpb.pps.projeto.modumender.annotations.Entity;
import br.edu.ifpb.pps.projeto.modumender.annotations.Id;
import br.edu.ifpb.pps.projeto.modumender.util.ConexaoDB;
import br.edu.ifpb.pps.projeto.modumender.SchemaGenerator;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * DAO genérico para operações de CRUD
 * em entidades anotadas com @Entity.
 */
public class GenericDAO<T> extends BaseDAO {

    private final Class<T> clazz;

    public GenericDAO(Class<T> clazz) {
        this.clazz = clazz;
        validateEntity();
    }

    /**
     * Verifica se a classe tem @Entity e @Id.
     */
    private void validateEntity() {
        if (!clazz.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException(
                    "A classe " + clazz.getName() + " não está anotada com @Entity."
            );
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
            throw new IllegalArgumentException(
                    "A classe " + clazz.getName() + " não possui um campo anotado com @Id."
            );
        }
    }

    /**
     * Insere ou atualiza a entidade no banco (aqui, só INSERIR).
     */
    public void save(T entity) throws SQLException {
        String sql = SchemaGenerator.generateInsert(entity);
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            int index = 1;
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Column.class) || field.isAnnotationPresent(Id.class)) {
                    field.setAccessible(true);
                    try {
                        Object value = field.get(entity);
                        // Validação "nullable=false"
                        Column colAnn = field.getAnnotation(Column.class);
                        if (colAnn != null && !colAnn.nullable() && value == null) {
                            throw new IllegalArgumentException("O campo " + field.getName() + " não pode ser nulo!");
                        }
                        stmt.setObject(index++, value);
                    } catch (IllegalAccessException e) {
                        throw new SQLException("Erro ao acessar o campo: " + field.getName(), e);
                    }
                }
            }
            stmt.executeUpdate();
        }
    }

    /**
     * Busca uma entidade pelo ID (assumindo ID do tipo int).
     */
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

    /**
     * Retorna todas as linhas da tabela referente à entidade.
     */
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

    /**
     * Exemplo de busca com filtros (WHERE) e ordenação.
     * filters: { "coluna" -> valor }
     */
    public List<T> findWithFilters(Map<String, Object> filters, String orderBy, boolean ascending) throws SQLException {
        // Nome da tabela
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
            // Remove o último " AND "
            query.setLength(query.length() - 5);
        }

        if (orderBy != null && !orderBy.isBlank()) {
            query.append(" ORDER BY ")
                    .append(orderBy)
                    .append(ascending ? " ASC" : " DESC");
        }

        List<T> results = new ArrayList<>();
        try (Connection conn = getConnection();
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

    /**
     * Mapear uma linha do ResultSet para a entidade.
     */
    private T mapResultSetToEntity(ResultSet rs) throws SQLException {
        try {
            T entity = clazz.getDeclaredConstructor().newInstance();
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                // Nome da coluna = nome do campo ou @Column(name)
                Column colAnn = field.getAnnotation(Column.class);
                Id idAnn = field.getAnnotation(Id.class);

                String columnName = null;
                if (colAnn != null && !colAnn.name().isEmpty()) {
                    columnName = colAnn.name();
                } else if (idAnn != null) {
                    // Se @Id tem um name diferente, podemos tratar. Aqui assumimos que seja igual ao nome do campo
                    columnName = field.getName();
                } else {
                    columnName = field.getName();
                }

                Object value = rs.getObject(columnName);
                field.set(entity, value);
            }
            return entity;
        } catch (Exception e) {
            throw new SQLException("Erro ao mapear ResultSet para a entidade: " + clazz.getName(), e);
        }
    }

    /**
     * Exemplo de deleção por ID (simples).
     */
    public void deleteById(int id) throws SQLException {
        // Precisamos do nome da tabela e do campo PK (assumindo "id").
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
}
