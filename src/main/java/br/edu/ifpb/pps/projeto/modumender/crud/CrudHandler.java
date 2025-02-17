package br.edu.ifpb.pps.projeto.modumender.crud;

import br.edu.ifpb.pps.projeto.modumender.dao.DAOFactory;
import br.edu.ifpb.pps.projeto.modumender.dao.GenericDAO;
import br.edu.ifpb.pps.projeto.modumender.http.HttpRequest;
import br.edu.ifpb.pps.projeto.modumender.http.HttpResponse;
import java.sql.SQLException;

/**
 * Handler genérico para as operações CRUD
 * (listAll, findById, create, update, delete).
 * Precisaria de "model binding" para create/update se quiser
 * popular automaticamente.
 */
public class CrudHandler {

    private final Class<?> entityClass;

    public CrudHandler(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    public Object listAll(HttpRequest req, HttpResponse resp) {
        try {
            GenericDAO<?> dao = DAOFactory.createDAO(entityClass);
            var lista = dao.findAll();
            return "List of " + entityClass.getSimpleName()
                    + ": count=" + lista.size();
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
                return entityClass.getSimpleName() + " not found, id=" + id;
            }
            return obj.toString(); // ou converter pra JSON
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
            return "Erro findById: " + e.getMessage();
        }
    }

    public Object create(HttpRequest req, HttpResponse resp) {
        try {
            Object newObj = entityClass.getDeclaredConstructor().newInstance();
            // TODO: model binding...
            GenericDAO dao = DAOFactory.createDAO(entityClass);
            dao.save(newObj);
            return "Created " + entityClass.getSimpleName();
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

            GenericDAO dao = DAOFactory.createDAO(entityClass);
            Object obj = dao.findById(id);
            if (obj == null) {
                resp.setStatus(404);
                return entityClass.getSimpleName() + " not found, id=" + id;
            }
            // TODO: model binding p/ atualizar fields
            dao.save(obj);
            return "Updated id=" + id;
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
                return "Not found to delete, id=" + id;
            }
            dao.deleteById(id);
            return "Deleted id=" + id;
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
            return "Erro delete: " + e.getMessage();
        }
    }
}
