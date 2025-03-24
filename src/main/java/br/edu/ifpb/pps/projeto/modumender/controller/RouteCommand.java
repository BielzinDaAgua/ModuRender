package br.edu.ifpb.pps.projeto.modumender.controller;

import br.edu.ifpb.pps.projeto.modumender.http.HttpRequest;
import br.edu.ifpb.pps.projeto.modumender.http.HttpResponse;

/**
 * Interface que encapsula a lógica de cada rota como um comando executável.
 */
public interface RouteCommand {
    /**
     Define um comando que pode ser executado por uma rota HTTP específica.
     Todo comando que responde a uma rota implementa esta interface.
     Quando uma rota é acessada, o framework executa o método execute.
     */
    Object execute(HttpRequest req, HttpResponse resp) throws Exception;
}