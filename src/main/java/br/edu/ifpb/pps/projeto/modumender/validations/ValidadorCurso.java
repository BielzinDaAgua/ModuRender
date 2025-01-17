package br.edu.ifpb.pps.projeto.modumender.validations;

import br.edu.ifpb.pps.projeto.modumender.models.Curso;

public class ValidadorCurso implements Validador<Curso> {
    @Override
    public void validar(Curso curso) throws IllegalArgumentException {
        if (curso.getTitulo() == null || curso.getTitulo().isEmpty()) {
            throw new IllegalArgumentException("O título do curso é obrigatório!");
        }
        if (curso.getDescricao() == null || curso.getDescricao().isEmpty()) {
            throw new IllegalArgumentException("A descrição do curso é obrigatória!");
        }
        if (curso.getPreco() <= 0) {
            throw new IllegalArgumentException("O preço do curso deve ser maior que zero!");
        }
        if (curso.getInstrutorId() <= 0) {
            throw new IllegalArgumentException("O ID do instrutor é inválido!");
        }
    }
}