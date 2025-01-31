package br.edu.ifpb.pps.projeto.modumender.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * Marca uma classe como Entidade para o framework.
 * O framework criará uma tabela com base nessa classe.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Entity {
    /**
     * Nome da tabela no banco de dados.
     * Se vazio, usará o nome da classe em minúsculo.
     */
    String tableName() default "";
}
