package br.edu.ifpb.pps.projeto.modumender.controller;


/**
 Representa uma rota HTTP concreta (GET /usuarios, POST /cadastro, etc).

 Associa um método HTTP e uma URL com um comando (RouteCommand) específico.

 Indica se a rota é REST ou não (importante para o tipo de resposta, como JSON).
 */
public class RouteDefinition {
    private final String httpMethod;
    private final String pathTemplate;
    private final RouteCommand command;
    private final boolean isRest;

    // Construtor principal
    public RouteDefinition(String httpMethod, String pathTemplate, RouteCommand command, boolean isRest) {
        this.httpMethod = httpMethod;
        this.pathTemplate = pathTemplate;
        this.command = command;
        this.isRest = isRest;
    }

    // Construtor auxiliar sem isRest
    public RouteDefinition(String httpMethod, String pathTemplate, RouteCommand command) {
        this(httpMethod, pathTemplate, command, false);
    }

    // Construtor ponte com ControllerHandler e isRest
    public RouteDefinition(String httpMethod, String pathTemplate, ControllerHandler handler, boolean isRest) {
        this(
                httpMethod,
                pathTemplate,
                new RouteCommandFunctional((req, resp) -> {
                    try {
                        return handler.invoke(req, resp);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }),
                isRest
        );
    }

    // Construtor ponte original (compatibilidade)
    public RouteDefinition(String httpMethod, String pathTemplate, ControllerHandler handler) {
        this(httpMethod, pathTemplate, handler, false);
    }

    // Getters
    public String getHttpMethod() {
        return httpMethod;
    }

    public String getPathTemplate() {
        return pathTemplate;
    }

    public RouteCommand getCommand() {
        return command;
    }

    public boolean isRest() {
        return isRest;
    }
}
