package br.edu.ifpb.pps.projeto.modumender.views;

import br.edu.ifpb.pps.projeto.modumender.annotations.TemplateRoute;
import java.util.Map;

@TemplateRoute(path="/home", template="home/home")
public class HomePage {

    public static Map<String,Object> buildModel() {
        return Map.of(
                "title", "Bem-vindo ao ModuRender!",
                "message", "Este é um framework MVC simples usando Java puro e Thymeleaf."
        );
    }
}
