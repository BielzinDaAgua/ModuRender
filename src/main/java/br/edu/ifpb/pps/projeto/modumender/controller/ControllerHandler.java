package br.edu.ifpb.pps.projeto.modumender.controller;

import java.lang.reflect.Method;

/**
 * Representa um controlador e um método que pode ser invocado.
 */
public class ControllerHandler {
    private final Class<?> controllerClass;
    private final Method method;

    public ControllerHandler(Class<?> controllerClass, Method method) {
        this.controllerClass = controllerClass;
        this.method = method;
    }

    public Object invoke() throws Exception {
        Object instance = controllerClass.getDeclaredConstructor().newInstance();
        return method.invoke(instance);
    }
}
