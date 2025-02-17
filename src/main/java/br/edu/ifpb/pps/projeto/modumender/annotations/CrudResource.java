package br.edu.ifpb.pps.projeto.modumender.annotations;

import java.lang.annotation.*;

/**
 * Indica que a classe define um "recurso" CRUD automático
 * para a entidade especificada.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CrudResource {
    String path();        // Ex: "/usuarios"
    Class<?> entity();    // Ex: Usuario.class
}
