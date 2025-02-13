package br.edu.ifpb.pps.projeto.modumender.dao;

import br.edu.ifpb.pps.projeto.modumender.annotations.Column;
import br.edu.ifpb.pps.projeto.modumender.annotations.Entity;
import br.edu.ifpb.pps.projeto.modumender.annotations.Id;
import br.edu.ifpb.pps.projeto.modumender.annotations.Min;
import br.edu.ifpb.pps.projeto.modumender.annotations.Max;
import br.edu.ifpb.pps.projeto.modumender.annotations.ExactDigits;
import br.edu.ifpb.pps.projeto.modumender.annotations.Length;

import br.edu.ifpb.pps.projeto.modumender.SchemaGenerator;
import br.edu.ifpb.pps.projeto.modumender.util.ConexaoDB;

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
public class GenericDAO<T> extends BaseDAO implements CrudRepository<T>  {

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

    /**
     * Insere a entidade no banco.
     * Inclui validações de null, Min, Max, Length, ExactDigits, etc.
     */
    public void save(T entity) throws SQLException {
        // 1) Monta o SQL via SchemaGenerator
        String sql = SchemaGenerator.generateInsert(entity);

        // 2) Abre conexão e statement
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            Field[] fields = clazz.getDeclaredFields();
            int index = 1;

            for (Field field : fields) {
                if (field.isAnnotationPresent(Column.class) || field.isAnnotationPresent(Id.class)) {
                    field.setAccessible(true);

                    // Lê o valor do campo
                    Object value;
                    try {
                        value = field.get(entity);
                    } catch (IllegalAccessException e) {
                        throw new SQLException("Erro ao acessar campo: " + field.getName(), e);
                    }

                    // Realiza validações
                    validateField(field, value);

                    // Seta no PreparedStatement
                    stmt.setObject(index++, value);
                }
            }

            // 3) Executa o INSERT
            stmt.executeUpdate();
        }
    }

    /**
     * Realiza as validações adicionais com base nas anotações
     * (@Column(nullable=false), @Min, @Max, @Length, @ExactDigits, etc.).
     */
    private void validateField(Field field, Object value) {
        // 1) Se @Column(nullable=false), checa null
        Column colAnn = field.getAnnotation(Column.class);
        if (colAnn != null && !colAnn.nullable() && value == null) {
            throw new IllegalArgumentException("O campo " + field.getName() + " não pode ser nulo!");
        }

        // 2) @Min
        if (field.isAnnotationPresent(Min.class)) {
            if (value == null) return; // se for null, já falha acima se for "nullable=false"
            Min minAnn = field.getAnnotation(Min.class);
            int minValue = minAnn.value();
            // Converte o valor para int/Integer
            int numericValue = convertToInt(value, field);
            if (numericValue < minValue) {
                throw new IllegalArgumentException("O campo " + field.getName() +
                        " deve ser >= " + minValue + ". Valor atual: " + numericValue);
            }
        }

        // 3) @Max
        if (field.isAnnotationPresent(Max.class)) {
            if (value == null) return;
            Max maxAnn = field.getAnnotation(Max.class);
            int maxValue = maxAnn.value();
            int numericValue = convertToInt(value, field);
            if (numericValue > maxValue) {
                throw new IllegalArgumentException("O campo " + field.getName() +
                        " deve ser <= " + maxValue + ". Valor atual: " + numericValue);
            }
        }

        // 4) @ExactDigits
        if (field.isAnnotationPresent(ExactDigits.class)) {
            if (value == null) return;
            ExactDigits edAnn = field.getAnnotation(ExactDigits.class);
            int expected = edAnn.value();

            // Se o campo for String, checamos length exato
            // Se for numérico, poderíamos converter para string e checar quantidade de dígitos
            String strValue = value.toString();
            if (strValue.length() != expected) {
                throw new IllegalArgumentException("O campo " + field.getName() +
                        " deve ter exatamente " + expected + " dígitos/caracteres. Valor: " + strValue);
            }
        }

        // 5) @Length (para strings)
        if (field.isAnnotationPresent(Length.class)) {
            if (value == null) return;
            Length lengthAnn = field.getAnnotation(Length.class);
            int minLen = lengthAnn.min();
            int maxLen = lengthAnn.max();

            // Supondo que se aplique a String
            if (!(value instanceof String)) {
                return; // ou disparar exceção se quiser
            }
            String strValue = (String) value;
            if (strValue.length() < minLen) {
                throw new IllegalArgumentException("O campo " + field.getName() +
                        " deve ter ao menos " + minLen + " caracteres. Valor atual: " + strValue);
            }
            if (strValue.length() > maxLen) {
                throw new IllegalArgumentException("O campo " + field.getName() +
                        " deve ter no máximo " + maxLen + " caracteres. Valor atual: " + strValue);
            }
        }
    }

    /**
     * Converte um valor para int (usado em @Min e @Max).
     * Se não for int ou Integer, podemos estender para double etc.
     */
    private int convertToInt(Object value, Field field) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        throw new IllegalArgumentException("O campo " + field.getName() + " não é numérico, mas tem @Min/@Max.");
    }

    // -------------------------------------------------------------------
    // Métodos de consulta (sem grandes mudanças, mas com extras)
    // -------------------------------------------------------------------

    /**
     * Busca pelo ID (assumindo que ID é int).
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
     * Retorna todas as linhas da tabela.
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
     * Pesquisa com filtros exatos e ordenação (igual antes).
     */
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
            // Remove último " AND "
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

    /**
     * Consulta por intervalo (ex.: data between start and end).
     * O campo deve ser numérico ou data.
     */
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

    /**
     * Consulta por like (parcial) em um campo String.
     */
    public List<T> findLike(String columnName, String pattern) throws SQLException {
        Entity entityAnn = clazz.getAnnotation(Entity.class);
        String tableName = entityAnn.tableName().isEmpty()
                ? clazz.getSimpleName().toLowerCase()
                : entityAnn.tableName();

        // pattern pode ser "%abc%", "abc%", "%abc"
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

    /**
     * Consulta por igualdade simples no campo especificado.
     */
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

    /**
     * Deleção por ID.
     */
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

    /**
     * Mapear uma linha do ResultSet para a entidade.
     */
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
            throw new SQLException("Erro ao mapear ResultSet para a entidade: " + clazz.getName(), e);
        }
    }
}
