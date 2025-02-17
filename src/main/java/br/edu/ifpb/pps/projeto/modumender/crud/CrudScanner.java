package br.edu.ifpb.pps.projeto.modumender.crud;

import br.edu.ifpb.pps.projeto.modumender.annotations.CrudResource;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Localiza classes @CrudResource para gerar CRUD automático.
 */
public class CrudScanner {

    private static final List<CrudResourceDefinition> definitions = new ArrayList<>();

    public static void scanCrudResources(String basePackage) {
        Reflections reflections = new Reflections(basePackage);
        Set<Class<?>> resources = reflections.getTypesAnnotatedWith(CrudResource.class);

        for (Class<?> c : resources) {
            CrudResource ann = c.getAnnotation(CrudResource.class);
            String path = ann.path();
            Class<?> entity = ann.entity();

            CrudResourceDefinition def = new CrudResourceDefinition(path, entity);
            definitions.add(def);

            System.out.println("CRUD Resource: "
                    + path + " -> " + entity.getSimpleName());
        }
    }

    public static List<CrudResourceDefinition> getDefinitions() {
        return definitions;
    }
}
