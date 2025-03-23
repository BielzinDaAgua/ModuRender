package br.edu.ifpb.pps.projeto.modumender.annotations;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Email {
    // Ex.: poderia ter "regex" se quiser customizar mais
}
