package br.edu.ifpb.pps.projeto.modumender.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * Indica relacionamento "muitos-para-muitos".
 * Precisamos de tabela de junção (joinTable).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ManyToMany {
    String joinTable();
    String joinColumn();
    String inverseJoinColumn();
}
