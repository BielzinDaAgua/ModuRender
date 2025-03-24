package br.edu.ifpb.pps.projeto.modumender.controller;

import br.edu.ifpb.pps.projeto.modumender.http.HttpRequest;
import br.edu.ifpb.pps.projeto.modumender.http.HttpResponse;

import java.util.function.BiFunction;

/**
 Permite definir comandos para rotas usando funções lambda diretamente.

 Mais simples e direto do que reflexão.

 É útil quando você não precisa criar controllers inteiros, apenas ações isoladas rápidas.
 */
public class RouteCommandFunctional implements RouteCommand {

    // Guarda a função que realiza a lógica da rota
    private final BiFunction<HttpRequest, HttpResponse, Object> function;

    public RouteCommandFunctional(BiFunction<HttpRequest, HttpResponse, Object> function) {
        this.function = function;
    }

    @Override
    public Object execute(HttpRequest req, HttpResponse resp) throws Exception {
        // A função retorna o objeto resultante (ou null)
        return function.apply(req, resp);
    }
}

/**
 * Exemplo pratico

 RouteCommand cmd = new RouteCommandFunctional((req, resp) -> {
 return "Resposta rápida";
 });

 Ao acessar essa rota, a resposta sempre será "Resposta rápida".
 */