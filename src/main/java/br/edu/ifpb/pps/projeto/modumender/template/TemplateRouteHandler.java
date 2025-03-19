package br.edu.ifpb.pps.projeto.modumender.template;

import br.edu.ifpb.pps.projeto.modumender.controller.TemplateController;
import br.edu.ifpb.pps.projeto.modumender.http.HttpRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class TemplateRouteHandler {
    private static final Map<String, Function<HttpRequest, String>> templateRoutes = new HashMap<>();

    static {
        templateRoutes.put("/home", req -> new TemplateController().renderHome(req));
        templateRoutes.put("/profile", req -> new TemplateController().renderProfile(req));
    }

    public static String handleRequest(String path, HttpRequest request) {
        Function<HttpRequest, String> handler = templateRoutes.get(path);
        if (handler != null) {
            return handler.apply(request);
        }
        return null; // Indica que não encontrou a rota
    }
}
