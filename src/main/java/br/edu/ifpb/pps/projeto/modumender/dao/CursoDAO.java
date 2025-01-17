package br.edu.ifpb.pps.projeto.modumender.dao;

import br.edu.ifpb.pps.projeto.modumender.models.Curso;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CursoDAO extends BaseDAO {
    public void save(Curso curso) throws SQLException {
        String sql = "INSERT INTO cursos (titulo, descricao, preco, data_criacao, instrutor_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, curso.getTitulo());
            stmt.setString(2, curso.getDescricao());
            stmt.setDouble(3, curso.getPreco());
            stmt.setDate(4, curso.getDataCriacao()); // Agora passamos um java.sql.Date
            stmt.setInt(5, curso.getInstrutorId());
            stmt.executeUpdate();

            // Obter o ID gerado automaticamente
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    curso.setId(rs.getInt(1));
                }
            }
        }
    }

    public List<Curso> findAll() throws SQLException {
        String sql = "SELECT * FROM cursos";
        List<Curso> cursos = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                cursos.add(new Curso(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("descricao"),
                        rs.getDouble("preco"),
                        rs.getDate("data_criacao"), // Convertendo para java.sql.Date
                        rs.getInt("instrutor_id")
                ));
            }
        }

        return cursos;
    }

    // Busca cursos por nome de categoria
    public List<Curso> findByCategoria(String categoriaNome) throws SQLException {
        String sql = "SELECT c.* " +
                "FROM cursos c " +
                "JOIN categorias cat ON c.id = cat.id " +
                "WHERE cat.nome ILIKE ?";
        List<Curso> cursos = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + categoriaNome + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    cursos.add(new Curso(
                            rs.getInt("id"),
                            rs.getString("titulo"),
                            rs.getString("descricao"),
                            rs.getDouble("preco"),
                            rs.getDate("data_criacao"),
                            rs.getInt("instrutor_id")
                    ));
                }
            }
        }
        return cursos;
    }
}
