package br.edu.ifpb.pps.projeto.modumender.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * Indica relacionamento "muitos-para-um".
 * O campo deve ser outra entidade.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ManyToOne {
    String referencedColumnName() default "id";
}
