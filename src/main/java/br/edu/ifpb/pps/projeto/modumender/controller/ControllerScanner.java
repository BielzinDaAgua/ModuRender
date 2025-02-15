package br.edu.ifpb.pps.projeto.modumender.controller;

import br.edu.ifpb.pps.projeto.modumender.annotations.Controller;
import br.edu.ifpb.pps.projeto.modumender.annotations.Route;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ControllerScanner {

    private static final Map<String, ControllerHandler> routes = new HashMap<>();

    public static void scanControllers(String basePackage) {
        Reflections reflections = new Reflections(basePackage);
        Set<Class<?>> controllers = reflections.getTypesAnnotatedWith(Controller.class);

        for (Class<?> controller : controllers) {
            for (Method method : controller.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Route.class)) {
                    Route route = method.getAnnotation(Route.class);
                    String key = route.method() + ":" + route.path(); // Ex: "GET:/exemplo"
                    routes.put(key, new ControllerHandler(controller, method));
                    System.out.println("Rota registrada: " + key);
                }
            }
        }
    }

    public static ControllerHandler getHandler(String method, String path) {
        return routes.get(method + ":" + path);
    }
}