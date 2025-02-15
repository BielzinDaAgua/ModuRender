package br.edu.ifpb.pps.projeto.modumender.controller;

import br.edu.ifpb.pps.projeto.modumender.annotations.Controller;
import br.edu.ifpb.pps.projeto.modumender.annotations.Route;

/**
 * Um exemplo de controlador que responde a requisições HTTP.
 */
@Controller
public class HomeController {

    @Route(path = "/hello", method = "GET")
    public String sayHello() {
        return "Olá, bem-vindo ao framework!";
    }
}