package br.edu.ifpb.pps.projeto.modumender.controller;

import java.util.Map;

/**
 * Representa uma rota manual (httpMethod + pathTemplate)
 * e o handler que pode ser invocado (ControllerHandler).
 */
public class RouteDefinition {
    private final String httpMethod;     // ex.: "GET"
    private final String pathTemplate;   // ex.: "/hello/{nome}"
    private final ControllerHandler handler;

    public RouteDefinition(String httpMethod, String pathTemplate, ControllerHandler handler) {
        this.httpMethod = httpMethod;
        this.pathTemplate = pathTemplate;
        this.handler = handler;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getPathTemplate() {
        return pathTemplate;
    }

    public ControllerHandler getHandler() {
        return handler;
    }
}
