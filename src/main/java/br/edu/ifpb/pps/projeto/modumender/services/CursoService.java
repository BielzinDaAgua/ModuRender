package br.edu.ifpb.pps.projeto.modumender.services;

import br.edu.ifpb.pps.projeto.modumender.dao.CursoDAO;
import br.edu.ifpb.pps.projeto.modumender.models.Curso;
import br.edu.ifpb.pps.projeto.modumender.validations.ValidadorCurso;

import java.sql.SQLException;
import java.util.List;

public class CursoService {
    private final CursoDAO cursoDAO = new CursoDAO();
    private final ValidadorCurso validadorCurso = new ValidadorCurso();

    public void salvarCurso(Curso curso) throws SQLException {
        validadorCurso.validar(curso); // Validações
        cursoDAO.save(curso);         // Persistência
    }

    public List<Curso> listarCursos() throws SQLException {
        return cursoDAO.findAll();
    }
}