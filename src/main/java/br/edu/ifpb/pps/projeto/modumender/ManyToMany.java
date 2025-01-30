package br.edu.ifpb.pps.projeto.modumender;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)  // Pode ser aplicada apenas em atributos
@Retention(RetentionPolicy.RUNTIME)  // Disponível em tempo de execução
public @interface ManyToMany {
    String joinTable();  // Nome da tabela de junção
    String joinColumn();  // Nome da coluna que referencia a entidade
    String inverseJoinColumn();  // Nome da coluna que referencia a outra entidade
}
