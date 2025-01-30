package br.edu.ifpb.pps.projeto.modumender.services;
import br.edu.ifpb.pps.projeto.modumender.dao.GenericDAO;

import java.sql.SQLException;
import java.util.List;

public class GenericService<T> {
    private final GenericDAO<T> genericDAO;

    public GenericService(Class<T> clazz) {
        this.genericDAO = new GenericDAO<>(clazz);
    }

    // Salvar uma entidade
    public void save(T entity) throws SQLException {
        genericDAO.save(entity);
    }

    // Buscar uma entidade pelo ID
    public T findById(int id) throws SQLException {
        return genericDAO.findById(id);
    }

    // Listar todas as entidades
    public List<T> findAll() throws SQLException {
        return genericDAO.findAll();
    }

    public void deleteById(int i) {
    }

    // Implementar métodos adicionais, caso necessário, como filtros avançados
}