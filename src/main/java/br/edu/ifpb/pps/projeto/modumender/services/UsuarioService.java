package br.edu.ifpb.pps.projeto.modumender.services;

import br.edu.ifpb.pps.projeto.modumender.dao.UsuarioDAO;
import br.edu.ifpb.pps.projeto.modumender.models.Usuario;
import br.edu.ifpb.pps.projeto.modumender.validations.ValidadorUsuario;


import java.sql.SQLException;
import java.util.List;


public class UsuarioService {
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final ValidadorUsuario validadorUsuario = new ValidadorUsuario();

    public void salvarUsuario(Usuario usuario) throws SQLException {
        validadorUsuario.validar(usuario); // Validações
        usuarioDAO.save(usuario);         // Persistência
    }

    public Usuario buscarUsuarioPorId(int id) throws SQLException {
        return usuarioDAO.findById(id);
    }

    public List<Usuario> buscarTodosUsuarios() throws SQLException {
        return usuarioDAO.findAll();
    }
}