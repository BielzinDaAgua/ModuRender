package br.edu.ifpb.pps.projeto.modumender.dao;

import br.edu.ifpb.pps.projeto.modumender.models.Matricula;
import br.edu.ifpb.pps.projeto.modumender.models.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MatriculaDAO extends BaseDAO {
    public void save(Matricula matricula) throws SQLException {
        String sql = "INSERT INTO matriculas (usuario_id, curso_id, data_matricula, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, matricula.getUsuarioId());
            stmt.setInt(2, matricula.getCursoId());
            stmt.setDate(3, matricula.getDataMatricula());
            stmt.setString(4, matricula.getStatus());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    matricula.setId(rs.getInt(1));
                }
            }
        }
    }

    public List<Matricula> findAll() throws SQLException {
        String sql = "SELECT * FROM matriculas";
        List<Matricula> matriculas = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                matriculas.add(new Matricula(
                        rs.getInt("id"),
                        rs.getInt("usuario_id"),
                        rs.getInt("curso_id"),
                        rs.getDate("data_matricula"),
                        rs.getString("status")
                ));
            }
        }

        return matriculas;
    }

    public List<Usuario> findAlunosByCursoId(int cursoId) throws SQLException {
        String sql = "SELECT u.* " +
                "FROM usuarios u " +
                "JOIN matriculas m ON u.id = m.usuario_id " +
                "WHERE m.curso_id = ?";
        List<Usuario> alunos = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cursoId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    alunos.add(new Usuario(
                            rs.getInt("id"),
                            rs.getString("nome"),
                            rs.getString("email"),
                            rs.getString("senha"),
                            rs.getString("tipo_usuario")
                    ));
                }
            }
        }
        return alunos;
    }
}
