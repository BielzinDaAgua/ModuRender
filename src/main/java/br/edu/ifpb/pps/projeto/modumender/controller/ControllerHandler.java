package br.edu.ifpb.pps.projeto.modumender.controller;

import br.edu.ifpb.pps.projeto.modumender.http.HttpRequest;
import br.edu.ifpb.pps.projeto.modumender.http.HttpResponse;

import java.lang.reflect.Method;

/**
 * Representa um "controlador" manual: (classe + método).
 * Chamado via reflection no invoke(...).
 */
public class ControllerHandler {
    protected final Class<?> controllerClass;
    protected final Method method;

    public ControllerHandler(Class<?> controllerClass, Method method) {
        this.controllerClass = controllerClass;
        this.method = method;
    }

    public Object invoke(HttpRequest request, HttpResponse response) throws Exception {
        if (controllerClass == null || method == null) {
            throw new IllegalStateException("ControllerHandler sem classe/método!");
        }
        Object instance = controllerClass.getDeclaredConstructor().newInstance();
        Class<?>[] paramTypes = method.getParameterTypes();
        if (paramTypes.length == 2
                && paramTypes[0].equals(HttpRequest.class)
                && paramTypes[1].equals(HttpResponse.class)) {
            return method.invoke(instance, request, response);
        }
        else if (paramTypes.length == 1
                && paramTypes[0].equals(HttpRequest.class)) {
            return method.invoke(instance, request);
        }
        else if (paramTypes.length == 0) {
            return method.invoke(instance);
        }
        throw new IllegalArgumentException("Método do controlador com parâmetros não suportados: " + method);
    }

}
