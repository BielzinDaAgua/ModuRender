package br.edu.ifpb.pps.projeto.modumender.controller;

import br.edu.ifpb.pps.projeto.modumender.http.HttpRequest;
import br.edu.ifpb.pps.projeto.modumender.http.HttpResponse;

import java.util.function.BiFunction;

/**
 * Um handler que invoca uma BiFunction<HttpRequest,HttpResponse,Object>
 * sem reflection.
 */
public class ControllerHandlerFunctional extends ControllerHandler {

    private final BiFunction<HttpRequest,HttpResponse,Object> function;

    public ControllerHandlerFunctional(BiFunction<HttpRequest,HttpResponse,Object> function) {
        super(null, null);
        this.function = function;
    }

    @Override
    public Object invoke(HttpRequest request, HttpResponse response) throws Exception {
        return function.apply(request, response);
    }
}
