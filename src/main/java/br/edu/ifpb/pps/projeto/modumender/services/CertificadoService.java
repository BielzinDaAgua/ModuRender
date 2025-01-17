package br.edu.ifpb.pps.projeto.modumender.services;

import br.edu.ifpb.pps.projeto.modumender.dao.CertificadoDAO;
import br.edu.ifpb.pps.projeto.modumender.models.Certificado;
import br.edu.ifpb.pps.projeto.modumender.validations.ValidadorCertificado;

import java.sql.SQLException;

public class CertificadoService {
    private final CertificadoDAO certificadoDAO = new CertificadoDAO();
    private final ValidadorCertificado validadorCertificado = new ValidadorCertificado();

    public void salvarCertificado(Certificado certificado) throws SQLException {
        validadorCertificado.validar(certificado); // Validações
        certificadoDAO.save(certificado);         // Persistência
    }
}