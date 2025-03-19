package br.edu.ifpb.pps.projeto.modumender.controller;

import br.edu.ifpb.pps.projeto.modumender.http.HttpRequest;
import br.edu.ifpb.pps.projeto.modumender.template.TemplateRenderer;

import java.util.HashMap;
import java.util.Map;

public class TemplateController {
    private final TemplateRenderer templateRenderer;

    public TemplateController() {
        this.templateRenderer = new TemplateRenderer();
    }

    public String renderHome(HttpRequest req) {
        Map<String, Object> model = new HashMap<>();
        model.put("title", "Bem-vindo ao ModoRender");
        model.put("message", "Este é um framework MVC simples usando Java puro e Thymeleaf.");
        return templateRenderer.render("home", model);
    }

    public String renderProfile(HttpRequest req) {
        String username = req.getParameter("username");
        if (username == null || username.isEmpty()) {
            username = "Usuário Desconhecido";
        }

        Map<String, Object> model = new HashMap<>();
        model.put("title", "Perfil de " + username);
        model.put("username", username);
        model.put("bio", "Usuário apaixonado por desenvolvimento Java puro!");
        return templateRenderer.render("profile", model);
    }
}
