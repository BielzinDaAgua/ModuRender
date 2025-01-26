package br.edu.ifpb.pps.projeto.modumender.services;

import br.edu.ifpb.pps.projeto.modumender.ModuRender;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class GenericService<T> {
    private final Class<T> clazz;

    public GenericService(Class<T> clazz) {
        this.clazz = clazz;
    }

    public void save(T entity) throws SQLException {
        ModuRender.save(entity);
    }

    public T findById(int id) throws SQLException {
        return ModuRender.findById(clazz, id);
    }

    public List<T> findAll() throws SQLException {
        return ModuRender.findAll(clazz);
    }

    public List<T> findWithFilters(Map<String, Object> filters, String orderBy, boolean ascending, String joinClause) throws SQLException {
        return ModuRender.findWithFilters(clazz, filters, orderBy, ascending, joinClause);
    }
}
