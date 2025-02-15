package br.edu.ifpb.pps.projeto.modumender.dao;

import java.sql.SQLException;
import java.util.List;

public interface CrudRepository<T> {
    void save(T entity) throws SQLException;
    T findById(int id) throws SQLException;
    List<T> findAll() throws SQLException;
    void deleteById(int id) throws SQLException;
}
