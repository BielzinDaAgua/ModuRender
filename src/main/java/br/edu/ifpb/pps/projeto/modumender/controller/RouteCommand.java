package br.edu.ifpb.pps.projeto.modumender.controller;

import br.edu.ifpb.pps.projeto.modumender.http.HttpRequest;
import br.edu.ifpb.pps.projeto.modumender.http.HttpResponse;

/**
 * Interface que encapsula a lógica de cada rota como um comando executável.
 */
public interface RouteCommand {
    /**
     * Executa a lógica da rota.
     * @return um objeto (por exemplo, uma entidade ou lista) que será serializado depois, ou null.
     */
    Object execute(HttpRequest req, HttpResponse resp) throws Exception;
}