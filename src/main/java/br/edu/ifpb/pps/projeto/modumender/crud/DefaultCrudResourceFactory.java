package br.edu.ifpb.pps.projeto.modumender.crud;


/**
 * Implementação padrão do Factory Method para criar CrudResourceDefinition.
 */
public class DefaultCrudResourceFactory implements CrudResourceFactory {

    @Override
    public CrudResourceDefinition createCrudResource(String path, Class<?> entity) {
        return new CrudResourceDefinition(path, entity);
    }
}
