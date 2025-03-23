package br.edu.ifpb.pps.projeto.modumender.controller;

import br.edu.ifpb.pps.projeto.modumender.annotations.Controller;
import br.edu.ifpb.pps.projeto.modumender.annotations.RestController;
import br.edu.ifpb.pps.projeto.modumender.annotations.Route;
import br.edu.ifpb.pps.projeto.modumender.dao.DAOFactory;
import br.edu.ifpb.pps.projeto.modumender.dao.GenericDAO;
import br.edu.ifpb.pps.projeto.modumender.http.HttpRequest;
import br.edu.ifpb.pps.projeto.modumender.http.HttpResponse;
import br.edu.ifpb.pps.projeto.modumender.model.Usuario;
import br.edu.ifpb.pps.projeto.modumender.rest.JsonUtil;
import br.edu.ifpb.pps.projeto.modumender.util.ValidationUtil;
import br.edu.ifpb.pps.projeto.modumender.util.ValidationException;
import jakarta.websocket.server.PathParam;

import java.util.List;

@RestController
public class UsuarioRestController {

    @Route(path="/api/usuarios", method="GET")
    public List<Usuario> listAll(HttpRequest req, HttpResponse resp) throws Exception {
        GenericDAO<Usuario> dao = DAOFactory.createDAO(Usuario.class);
        List<Usuario> usuarios = dao.findAll();
        return usuarios != null ? usuarios : List.of();
    }

    @Route(path="/api/usuarios", method="POST")
    public Usuario create(HttpRequest req, HttpResponse resp) throws Exception {
        String body = req.getRawRequest().getReader().lines()
                .reduce("", (acc, line) -> acc + line);

        Usuario novo = JsonUtil.fromJson(body, Usuario.class);

        try {
            ValidationUtil.validate(novo);
        } catch (ValidationException e) {
            resp.setStatus(400);
            resp.setContentType("text/plain"); // <<<<< importante!
            resp.writeBody("Erro de validação: " + e.getMessage());
            return null;
        }

        GenericDAO<Usuario> dao = DAOFactory.createDAO(Usuario.class);
        dao.save(novo);

        resp.setStatus(201);
        resp.setContentType("application/json");
        return novo;
    }


    @Route(path="/api/usuarios/{id}", method="PUT")
    public Usuario update(@PathParam("id") Integer id, HttpRequest req, HttpResponse resp) throws Exception {
        String body = req.getRawRequest().getReader().lines()
                .reduce("", (acc, line) -> acc + line);

        Usuario atualizado = JsonUtil.fromJson(body, Usuario.class);
        atualizado.setId(id);

        try {
            ValidationUtil.validate(atualizado);
        } catch (ValidationException e) {
            resp.setStatus(400);
            resp.writeBody("Erro de validação: " + e.getMessage());
            return null;
        }

        GenericDAO<Usuario> dao = DAOFactory.createDAO(Usuario.class);
        Usuario existe = dao.findById(id);
        if (existe == null) {
            resp.setStatus(404);
            resp.writeBody("Usuário não encontrado!");
            return null;
        }

        dao.update(atualizado);
        resp.setStatus(200);
        return atualizado;
    }

}
