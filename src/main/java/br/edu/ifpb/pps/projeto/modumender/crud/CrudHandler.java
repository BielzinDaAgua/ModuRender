package br.edu.ifpb.pps.projeto.modumender.crud;

import br.edu.ifpb.pps.projeto.modumender.dao.DAOFactory;
import br.edu.ifpb.pps.projeto.modumender.dao.GenericDAO;
import br.edu.ifpb.pps.projeto.modumender.http.HttpRequest;
import br.edu.ifpb.pps.projeto.modumender.http.HttpResponse;
import br.edu.ifpb.pps.projeto.modumender.rest.JsonUtil;
import br.edu.ifpb.pps.projeto.modumender.util.ValidationUtil;
import br.edu.ifpb.pps.projeto.modumender.util.ValidationException;

import java.io.BufferedReader;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class CrudHandler {

    private final Class<?> entityClass;

    public CrudHandler(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    public Object listAll(HttpRequest req, HttpResponse resp) {
        try {
            GenericDAO<?> dao = DAOFactory.createDAO(entityClass);
            List<?> lista = dao.findAll();
            resp.setContentType("application/json");
            return lista;
        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(500);
            return "Erro listAll: " + e.getMessage();
        }
    }

    public Object findById(HttpRequest req, HttpResponse resp) {
        try {
            String idStr = req.getPathParam("id");
            int id = Integer.parseInt(idStr);

            GenericDAO<?> dao = DAOFactory.createDAO(entityClass);
            Object obj = dao.findById(id);

            if (obj == null) {
                resp.setStatus(404);
                return entityClass.getSimpleName() + " não encontrado (id=" + id + ")";
            }

            resp.setContentType("application/json");
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
            return "Erro findById: " + e.getMessage();
        }
    }

    public Object create(HttpRequest req, HttpResponse resp) {
        try {
            String body = req.getRawRequest().getReader().lines().collect(Collectors.joining());

            Object newObj = JsonUtil.fromJson(body, entityClass);

            try {
                ValidationUtil.validate(newObj);
            } catch (ValidationException ve) {
                resp.setStatus(400);
                return "Erro de validação: " + ve.getMessage();
            }

            GenericDAO dao = DAOFactory.createDAO(entityClass);
            dao.save(newObj);

            resp.setStatus(201);
            resp.setContentType("application/json");
            return newObj;
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
            return "Erro create: " + e.getMessage();
        }
    }

    public Object update(HttpRequest req, HttpResponse resp) {
        try {
            String idStr = req.getPathParam("id");
            int id = Integer.parseInt(idStr);
            String body = req.getRawRequest().getReader().lines().collect(Collectors.joining());

            Object updatedObj = JsonUtil.fromJson(body, entityClass);

            GenericDAO dao = DAOFactory.createDAO(entityClass);
            Object existingObj = dao.findById(id);

            if (existingObj == null) {
                resp.setStatus(404);
                return entityClass.getSimpleName() + " não encontrado (id=" + id + ")";
            }

            // Define o ID no objeto atualizado via reflexão
            entityClass.getDeclaredMethod("setId", Integer.class).invoke(updatedObj, id);

            try {
                ValidationUtil.validate(updatedObj);
            } catch (ValidationException ve) {
                resp.setStatus(400);
                return "Erro de validação: " + ve.getMessage();
            }

            dao.update(updatedObj);
            resp.setContentType("application/json");
            return updatedObj;
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
            return "Erro update: " + e.getMessage();
        }
    }

    public Object delete(HttpRequest req, HttpResponse resp) {
        try {
            String idStr = req.getPathParam("id");
            int id = Integer.parseInt(idStr);

            GenericDAO dao = DAOFactory.createDAO(entityClass);
            Object obj = dao.findById(id);
            if (obj == null) {
                resp.setStatus(404);
                return entityClass.getSimpleName() + " não encontrado (id=" + id + ")";
            }

            dao.deleteById(id);
            resp.setStatus(200);
            return entityClass.getSimpleName() + " deletado com sucesso (id=" + id + ")";
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
            return "Erro delete: " + e.getMessage();
        }
    }
}
