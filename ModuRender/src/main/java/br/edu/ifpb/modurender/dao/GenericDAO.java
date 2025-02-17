package br.edu.ifpb.modurender.dao;

import br.edu.ifpb.modurender.utils.HibernateUtil;
import org.hibernate.Session;

import java.util.List;

public class GenericDAO<T> {

    private final Class<T> entityClass;

    public GenericDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * Salva ou atualiza uma entidade no banco usando Hibernate.
     */
    public void save(T entity) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(entity);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao salvar entidade: " + e.getMessage());
        }
    }

    /**
     * Busca todas as entidades do tipo especificado.
     */
    public List<T> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from " + entityClass.getSimpleName(), entityClass).list();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar entidades: " + e.getMessage());
        }
    }
}
