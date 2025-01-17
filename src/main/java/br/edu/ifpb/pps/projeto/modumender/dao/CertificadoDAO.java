package br.edu.ifpb.pps.projeto.modumender.dao;

import br.edu.ifpb.pps.projeto.modumender.models.Certificado;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CertificadoDAO extends BaseDAO {
    public void save(Certificado certificado) throws SQLException {
        String sql = "INSERT INTO certificados (usuario_id, curso_id, data_emissao) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, certificado.getUsuarioId());
            stmt.setInt(2, certificado.getCursoId());
            stmt.setDate(3, certificado.getDataEmissao());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    certificado.setId(rs.getInt(1));
                }
            }
        }
    }

    public List<Certificado> findAll() throws SQLException {
        String sql = "SELECT * FROM certificados";
        List<Certificado> certificados = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                certificados.add(new Certificado(
                        rs.getInt("id"),
                        rs.getInt("usuario_id"),
                        rs.getInt("curso_id"),
                        rs.getDate("data_emissao")
                ));
            }
        }

        return certificados;
    }
}
