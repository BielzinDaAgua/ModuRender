package br.edu.ifpb.pps.projeto.modumender.dao;

import br.edu.ifpb.pps.projeto.modumender.models.Avaliacao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AvaliacaoDAO extends BaseDAO {

    // Salvar uma avaliação no banco de dados
    public void save(Avaliacao avaliacao) throws SQLException {
        String sql = "INSERT INTO avaliacoes (usuario_id, curso_id, nota, comentario, data_avaliacao) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, avaliacao.getUsuarioId());
            stmt.setInt(2, avaliacao.getCursoId());
            stmt.setInt(3, avaliacao.getNota());
            stmt.setString(4, avaliacao.getComentario());
            stmt.setDate(5, avaliacao.getDataAvaliacao()); // Usar java.sql.Date para salvar a data
            stmt.executeUpdate();

            // Obter o ID gerado automaticamente
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    avaliacao.setId(rs.getInt(1)); // Atualizar o ID gerado
                }
            }
        }
    }

    // Listar todas as avaliações
    public List<Avaliacao> findAll() throws SQLException {
        String sql = "SELECT * FROM avaliacoes";
        List<Avaliacao> avaliacoes = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                avaliacoes.add(new Avaliacao(
                        rs.getInt("id"),
                        rs.getInt("usuario_id"),
                        rs.getInt("curso_id"),
                        rs.getInt("nota"),
                        rs.getString("comentario"),
                        rs.getDate("data_avaliacao") // Recuperar java.sql.Date
                ));
            }
        }

        return avaliacoes;
    }

    // Buscar avaliações de um curso ordenadas por nota (descendente)
    public List<Avaliacao> findAvaliacoesByCursoId(int cursoId) throws SQLException {
        String sql = "SELECT * FROM avaliacoes WHERE curso_id = ? ORDER BY nota DESC";
        List<Avaliacao> avaliacoes = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cursoId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    avaliacoes.add(new Avaliacao(
                            rs.getInt("id"),
                            rs.getInt("usuario_id"),
                            rs.getInt("curso_id"),
                            rs.getInt("nota"),
                            rs.getString("comentario"),
                            rs.getDate("data_avaliacao") // Recuperar java.sql.Date
                    ));
                }
            }
        }

        return avaliacoes;
    }

    // Calcular a média de notas de um curso
    public double calcularMediaNotasPorCurso(int cursoId) throws SQLException {
        String sql = "SELECT AVG(nota) AS media FROM avaliacoes WHERE curso_id = ?";
        double media = 0;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cursoId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    media = rs.getDouble("media"); // Pega a média das notas
                }
            }
        }

        return media;
    }

    // Buscar uma avaliação pelo ID
    public Avaliacao findById(int id) throws SQLException {
        String sql = "SELECT * FROM avaliacoes WHERE id = ?";
        Avaliacao avaliacao = null;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    avaliacao = new Avaliacao(
                            rs.getInt("id"),
                            rs.getInt("usuario_id"),
                            rs.getInt("curso_id"),
                            rs.getInt("nota"),
                            rs.getString("comentario"),
                            rs.getDate("data_avaliacao") // Recuperar java.sql.Date
                    );
                }
            }
        }

        return avaliacao;
    }
}
