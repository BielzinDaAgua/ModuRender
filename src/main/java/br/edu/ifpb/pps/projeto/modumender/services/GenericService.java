package br.edu.ifpb.pps.projeto.modumender.services;

import br.edu.ifpb.pps.projeto.modumender.dao.GenericDAO;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Camada de serviço genérica que chama os DAOs,
 * podendo ter validações adicionais, lógica de negócio, etc.
 */
public class GenericService<T> {

    private final GenericDAO<T> dao;

    public GenericService(Class<T> clazz) {
        this.dao = new GenericDAO<>(clazz);
    }

    public void save(T entity) throws SQLException {
        // Se quiser adicionar validações de negócio antes de salvar, faça aqui
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
