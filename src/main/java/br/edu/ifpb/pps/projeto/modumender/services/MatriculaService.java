package br.edu.ifpb.pps.projeto.modumender.services;

import br.edu.ifpb.pps.projeto.modumender.dao.MatriculaDAO;
import br.edu.ifpb.pps.projeto.modumender.models.Matricula;
import br.edu.ifpb.pps.projeto.modumender.validations.ValidadorMatricula;

import java.sql.SQLException;

public class MatriculaService {
    private final MatriculaDAO matriculaDAO = new MatriculaDAO();
    private final ValidadorMatricula validadorMatricula = new ValidadorMatricula();

    public void salvarMatricula(Matricula matricula) throws SQLException {
        validadorMatricula.validar(matricula); // Validações
        matriculaDAO.save(matricula);         // Persistência
    }
}
