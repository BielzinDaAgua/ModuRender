package br.edu.ifpb.modurender.core;

import br.edu.ifpb.modurender.utils.DynamicEntityGenerator;
import br.edu.ifpb.modurender.utils.HibernateUtil;

import java.util.List;

public class EntityProcessor {

    /**
     * Adiciona uma nova entidade dinâmica.
     *
     * @param entityName Nome da entidade.
     * @param attributes Lista de atributos no formato "nome : tipo".
     */
    public void addEntity(String entityName, List<String> attributes) throws Exception {
        // Gera a classe dinâmica
        Class<?> dynamicEntity = DynamicEntityGenerator.generateEntity(entityName, attributes);

        // Registra a entidade no Hibernate
        HibernateUtil.addEntity(dynamicEntity);

        System.out.println("Entidade " + entityName + " registrada com sucesso!");
    }
}
