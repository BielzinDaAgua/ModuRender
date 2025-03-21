package br.edu.ifpb.pps.projeto.modumender.annotations;

import java.lang.annotation.*;

/**
 * Marca uma classe que define uma rota de template.
 * Exemplo:
 *
 * @TemplateRoute(path="/calc", template="calculadora")
 * public class CalculadoraPage { ... }
 *
 * Ao escanear, o framework criará uma rota "/calc" que renderiza "calculadora.html"
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TemplateRoute {
    String path();
    String template();
}
