package br.edu.ifpb.pps.projeto.modumender.validations;

import br.edu.ifpb.pps.projeto.modumender.models.Avaliacao;

public class ValidadorAvaliacao implements Validador<Avaliacao> {
    @Override
    public void validar(Avaliacao avaliacao) throws IllegalArgumentException {
        if (avaliacao.getNota() < 1 || avaliacao.getNota() > 5) {
            throw new IllegalArgumentException("A nota deve estar entre 1 e 5!");
        }
        if (avaliacao.getUsuarioId() <= 0) {
            throw new IllegalArgumentException("O ID do usuário é inválido!");
        }
        if (avaliacao.getCursoId() <= 0) {
            throw new IllegalArgumentException("O ID do curso é inválido!");
        }
        if (avaliacao.getComentario() == null || avaliacao.getComentario().length() < 10) {
            throw new IllegalArgumentException("O comentário deve ter pelo menos 10 caracteres!");
        }
    }
}