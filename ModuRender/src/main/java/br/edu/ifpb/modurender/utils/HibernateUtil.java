package br.edu.ifpb.modurender.utils;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.ArrayList;
import java.util.List;

public class HibernateUtil {

    private static final StandardServiceRegistry registry;
    private static final MetadataSources metadataSources;
    private static SessionFactory sessionFactory;
    private static final List<Class<?>> dynamicEntities = new ArrayList<>();

    static {
        try {
            // Configuração inicial do Hibernate
            registry = new StandardServiceRegistryBuilder()
                    .applySetting("hibernate.connection.driver_class", "org.postgresql.Driver")
                    .applySetting("hibernate.connection.url", "jdbc:postgresql://localhost:5432/modurender")
                    .applySetting("hibernate.connection.username", "postgres")
                    .applySetting("hibernate.connection.password", "12345")
                    .applySetting("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
                    .applySetting("hibernate.hbm2ddl.auto", "update") // Define para criar/atualizar tabelas
                    .applySetting("hibernate.show_sql", "true")       // Log das consultas SQL
                    .applySetting("hibernate.format_sql", "true")    // SQL formatado
                    .build();

            metadataSources = new MetadataSources(registry);

        } catch (Exception ex) {
            System.err.println("Erro ao configurar o Hibernate: " + ex.getMessage());
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Adiciona uma entidade dinâmica ao Hibernate.
     */
    public static void addEntity(Class<?> entityClass) {
        try {
            dynamicEntities.add(entityClass);
            metadataSources.addAnnotatedClass(entityClass);
            System.out.println("Entidade adicionada: " + entityClass.getName());
        } catch (Exception e) {
            System.err.println("Erro ao registrar entidade: " + entityClass.getName());
            e.printStackTrace();
        }
    }

    /**
     * Inicializa o SessionFactory após registrar todas as entidades.
     */
    public static void initializeSessionFactory() {
        if (sessionFactory == null) {
            try {
                Metadata metadata = metadataSources.buildMetadata();
                sessionFactory = metadata.buildSessionFactory();
                System.out.println("SessionFactory inicializado com sucesso.");
            } catch (Exception e) {
                System.err.println("Erro ao inicializar o SessionFactory: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Retorna o SessionFactory.
     */
    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            initializeSessionFactory();
        }
        return sessionFactory;
    }

    /**
     * Fecha o SessionFactory e o registro.
     */
    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
        StandardServiceRegistryBuilder.destroy(registry);
    }
}
