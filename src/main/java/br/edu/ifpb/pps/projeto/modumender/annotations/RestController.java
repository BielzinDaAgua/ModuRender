package br.edu.ifpb.pps.projeto.modumender.annotations;

import java.lang.annotation.*;

/**
 * Marca um controlador que produz/consome JSON (REST).
 * Diferente de @Controller (HTML), neste iremos
 * serializar automaticamente o retorno como JSON.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RestController {
}
