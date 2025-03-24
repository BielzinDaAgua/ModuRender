package br.edu.ifpb.pps.projeto.modumender.controller;

import br.edu.ifpb.pps.projeto.modumender.http.HttpRequest;
import br.edu.ifpb.pps.projeto.modumender.http.HttpResponse;

import java.util.function.BiFunction;

/**
 * Um Command simples que recebe uma função (lambda) e a executa.
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