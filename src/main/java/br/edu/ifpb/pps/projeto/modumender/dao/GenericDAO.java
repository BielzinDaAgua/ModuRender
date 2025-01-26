package br.edu.ifpb.pps.projeto.modumender.dao;

import br.edu.ifpb.pps.projeto.modumender.SchemaGenerator;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class GenericDAO<T> {
    private static SchemaGenerator SchemaGenerator;
    private final Class<T> clazz;

    public GenericDAO(Class<T> clazz) {
        this.clazz = clazz;
    }

    public void save(T entity) throws SQLException {
        // Usar reflexão para montar o SQL com base nas anotações
        SchemaGenerator.generateInsert(entity); // Exemplo de método
    }

    public T findById(int id) throws SQLException {
        // Usar reflexão para mapear o resultado para a entidade
        return SchemaGenerator.generateFindById(clazz, id);
    }

    public List<T> findAll() throws SQLException {
        // Usar reflexão para listar todas as instâncias
        return SchemaGenerator.generateFindAll(clazz);
    }
}
