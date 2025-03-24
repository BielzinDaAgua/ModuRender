package br.edu.ifpb.pps.projeto.modumender.template;

import br.edu.ifpb.pps.projeto.modumender.annotations.TemplateRoute;
import org.reflections.Reflections;
import java.util.*;

public class TemplateRouteScanner {
    private static final Map<String, TemplateAutoDefinition> routes = new HashMap<>();

    static {
        //Escaneia o pacote "br.edu.ifpb.pps.projeto.modumender.views"
        Reflections reflections = new Reflections("br.edu.ifpb.pps.projeto.modumender.views");
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(TemplateRoute.class);

        for (Class<?> cls : annotated) {
            TemplateRoute ann = cls.getAnnotation(TemplateRoute.class);
            String path = ann.path();
            String tpl  = ann.template();

            TemplateAutoDefinition def = new TemplateAutoDefinition(path, tpl, cls);
            routes.put(path, def);

            System.out.println("🔹 TemplateRoute encontrada: " + path + " -> " + tpl);
        }
    }

    public static TemplateAutoDefinition getDefinition(String path) {
        return routes.get(path);
    }
}
