package br.edu.ifpb.pps.projeto.modumender;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)  // Pode ser aplicada apenas em atributos
@Retention(RetentionPolicy.RUNTIME)  // Disponível em tempo de execução
public @interface ManyToOne {
    String referencedColumnName() default "id";  // Define a coluna de referência
}
