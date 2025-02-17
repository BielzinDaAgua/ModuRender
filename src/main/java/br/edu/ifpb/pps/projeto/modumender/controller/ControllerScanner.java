package br.edu.ifpb.pps.projeto.modumender.controller;

import br.edu.ifpb.pps.projeto.modumender.annotations.Controller;
import br.edu.ifpb.pps.projeto.modumender.annotations.Route;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Scanner que localiza classes @Controller e métodos @Route,
 * gerando "RouteDefinition" para cada rota manual.
 */
public class ControllerScanner {

    private static final List<RouteDefinition> routeDefinitions = new ArrayList<>();

    /**
     * Escaneia o pacote e registra rotas dos controladores manuais.
     */
    public static void scanControllers(String basePackage) {
        Reflections reflections = new Reflections(basePackage);
        Set<Class<?>> controllerClasses = reflections.getTypesAnnotatedWith(Controller.class);

        System.out.println("📌 Controladores encontrados:");
        for (Class<?> ctrlClass : controllerClasses) {
            System.out.println("  🔹 " + ctrlClass.getName());
        }

        for (Class<?> ctrlClass : controllerClasses) {
            for (Method m : ctrlClass.getDeclaredMethods()) {
                if (m.isAnnotationPresent(Route.class)) {
                    Route routeAnn = m.getAnnotation(Route.class);
                    String methodHttp = routeAnn.method().toUpperCase();
                    String pathTempl  = routeAnn.path();

                    ControllerHandler handler = new ControllerHandler(ctrlClass, m);
                    RouteDefinition def = new RouteDefinition(methodHttp, pathTempl, handler);
                    routeDefinitions.add(def);

                    System.out.println("✅ Rota registrada: " + methodHttp + " " + pathTempl + " -> " + ctrlClass.getSimpleName() + "." + m.getName());
                }
            }
        }

    }


    /**
     * Permite adicionar rotas programaticamente (ex. para CRUD).
     */
    public static void addRoute(String method, String pathTemplate, ControllerHandler handler) {
        RouteDefinition def = new RouteDefinition(method, pathTemplate, handler);
        routeDefinitions.add(def);
        System.out.println("Rota registrada (programática): " + method + ":" + pathTemplate);
    }

    public static List<RouteDefinition> getRouteDefinitions() {
        System.out.println("📜 Retornando rotas do ControllerScanner: " + routeDefinitions.size());
        for (RouteDefinition rd : routeDefinitions) {
            System.out.println("   ➡ " + rd.getHttpMethod() + " " + rd.getPathTemplate());
        }
        return routeDefinitions;
    }

}
