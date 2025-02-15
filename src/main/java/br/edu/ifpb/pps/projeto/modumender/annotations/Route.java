package br.edu.ifpb.pps.projeto.modumender.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mapeia um método de um controlador para uma URL específica.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Route {
    String path();    // Define o caminho da URL
    String method();  // Define o método HTTP (GET, POST, etc.)
}