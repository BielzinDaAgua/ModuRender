package br.edu.ifpb.pps.projeto.modumender.services;

import br.edu.ifpb.pps.projeto.modumender.dao.AvaliacaoDAO;
import br.edu.ifpb.pps.projeto.modumender.models.Avaliacao;
import br.edu.ifpb.pps.projeto.modumender.validations.ValidadorAvaliacao;

import java.sql.SQLException;

public class AvaliacaoService {
    private final AvaliacaoDAO avaliacaoDAO = new AvaliacaoDAO();
    private final ValidadorAvaliacao validadorAvaliacao = new ValidadorAvaliacao();

    public void salvarAvaliacao(Avaliacao avaliacao) throws SQLException {
        validadorAvaliacao.validar(avaliacao); // Validações
        avaliacaoDAO.save(avaliacao);         // Persistência
    }
}