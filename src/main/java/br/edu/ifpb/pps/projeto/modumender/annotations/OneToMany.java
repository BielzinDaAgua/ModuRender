package br.edu.ifpb.pps.projeto.modumender.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * Relacionamento "um-para-muitos".
 * O campo deve ser List<OutraEntidade>.
 * Em JPA real, seria complementado por "mappedBy", etc.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface OneToMany {
    Class<?> mappedBy();
}
