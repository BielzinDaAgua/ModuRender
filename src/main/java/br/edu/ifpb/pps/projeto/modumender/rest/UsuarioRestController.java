package br.edu.ifpb.pps.projeto.modumender.rest;

import br.edu.ifpb.pps.projeto.modumender.annotations.RestController;
import br.edu.ifpb.pps.projeto.modumender.annotations.Route;
import br.edu.ifpb.pps.projeto.modumender.dao.DAOFactory;
import br.edu.ifpb.pps.projeto.modumender.dao.GenericDAO;
import br.edu.ifpb.pps.projeto.modumender.http.HttpRequest;
import br.edu.ifpb.pps.projeto.modumender.http.HttpResponse;
import br.edu.ifpb.pps.projeto.modumender.model.Usuario;
import jakarta.websocket.server.PathParam;

import java.sql.SQLException;
import java.util.List;
import java.io.IOException;

@RestController
public class UsuarioRestController {

    // Ex.: GET /api/usuarios
    @Route(path="/api/usuarios", method="GET")
    public List<Usuario> listAll(HttpRequest req, HttpResponse resp) throws SQLException {
        GenericDAO<Usuario> dao = DAOFactory.createDAO(Usuario.class);
        List<Usuario> usuarios = dao.findAll();
        return (usuarios != null) ? usuarios : List.of();
    }


    // Ex.: POST /api/usuarios
    // Recebe JSON e retorna o objeto criado
    @Route(path="/api/usuarios", method="POST")
    public Usuario create(HttpRequest req, HttpResponse resp) throws Exception {
        String body = req.getRawRequest().getReader().lines()
                .reduce("", (acc, line) -> acc + line);

        Usuario novo = JsonUtil.fromJson(body, Usuario.class);

        // Validação
        if (novo.getNome() == null || novo.getNome().isEmpty()) {
            resp.setStatus(400);
            throw new IllegalArgumentException("Nome é obrigatório!");
        }

        GenericDAO<Usuario> dao = DAOFactory.createDAO(Usuario.class);
        dao.save(novo);
        resp.setStatus(201); // Created
        return novo;
    }

    @Route(path="/api/usuarios/{id}", method="PUT")
    public Usuario update(@PathParam("id") int id, HttpRequest req, HttpResponse resp) throws Exception {
        String body = req.getRawRequest().getReader().lines()
                .reduce("", (acc, line) -> acc + line);

        Usuario atualizado = JsonUtil.fromJson(body, Usuario.class);
        atualizado.setId(id);

        GenericDAO<Usuario> dao = DAOFactory.createDAO(Usuario.class);
        if (dao.findById(id) == null) {
            resp.setStatus(404);
            throw new IllegalArgumentException("Usuário não encontrado!");
        }

        dao.update(atualizado);
        return atualizado;
    }





}
