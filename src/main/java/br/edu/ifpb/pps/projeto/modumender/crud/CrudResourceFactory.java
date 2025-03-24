package br.edu.ifpb.pps.projeto.modumender.crud;


/**
 * Interface do Factory Method para criar definições de CRUD.
 */
public interface CrudResourceFactory {
    CrudResourceDefinition createCrudResource(String path, Class<?> entity);
}
