package br.edu.ifpb.pps.projeto.modumender.dao;

/**
 * Simple Factory para criação de DAOs,
 * evitando que o desenvolvedor crie "new GenericDAO<>(...)"
 * manualmente.
 */
public class DAOFactory {

    /**
     * Cria (ou retorna) um DAO para a classe de entidade informada.
     * Pode ser expandido para retornar DAOs especializados,
     * caches, singletons, etc.
     */
    public static <T> GenericDAO<T> createDAO(Class<T> entityClass) {
        return new GenericDAO<>(entityClass);
    }
}
