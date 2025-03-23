package br.edu.ifpb.pps.projeto.modumender.annotations;

import java.lang.annotation.*;

@Target(ElementType.FIELD)      // só em atributos
@Retention(RetentionPolicy.RUNTIME)
public @interface NotEmpty {
    // sem parâmetros, pois basta verificar se string está vazia
}
