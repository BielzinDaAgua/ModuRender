package br.edu.ifpb.pps.projeto.modumender.controller;

public class RouteDefinition {
    private final String httpMethod;
    private final String pathTemplate;
    private final RouteCommand command;

    // Construtor “principal” – recebe um RouteCommand pronto
    public RouteDefinition(String httpMethod, String pathTemplate, RouteCommand command) {
        this.httpMethod = httpMethod;
        this.pathTemplate = pathTemplate;
        this.command = command;
    }

    // Construtor “ponte” – recebe ControllerHandler e cria um RouteCommand
    public RouteDefinition(String httpMethod, String pathTemplate, ControllerHandler handler) {
        this(
                httpMethod,
                pathTemplate,
                new RouteCommandFunctional((req, resp) -> {
                    try {
                        return handler.invoke(req, resp);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
        );
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getPathTemplate() {
        return pathTemplate;
    }

    public RouteCommand getCommand() {
        return command;
    }
}
