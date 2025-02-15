package br.edu.ifpb.pps.projeto.modumender.services;

import br.edu.ifpb.pps.projeto.modumender.dao.GenericDAO;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class GenericService<T> {

    private final GenericDAO<T> dao;

    public GenericService(Class<T> clazz) {
        this.dao = new GenericDAO<>(clazz);
    }

    // Novo construtor para injetar o DAO diretamente
    public GenericService(GenericDAO<T> dao) {
        this.dao = dao;
    }

    public void save(T entity) throws SQLException {
        dao.save(entity);
    }


    public T findById(int id) throws SQLException {
        return dao.findById(id);
    }

    public List<T> findAll() throws SQLException {
        return dao.findAll();
    }

    public List<T> findWithFilters(Map<String, Object> filters, String orderBy, boolean ascending) throws SQLException {
        return dao.findWithFilters(filters, orderBy, ascending);
    }

    public void deleteById(int id) throws SQLException {
        dao.deleteById(id);
    }
}
