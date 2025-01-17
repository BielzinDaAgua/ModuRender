package br.edu.ifpb.pps.projeto.modumender.validations;

import br.edu.ifpb.pps.projeto.modumender.models.Matricula;

public class ValidadorMatricula implements Validador<Matricula> {
    @Override
    public void validar(Matricula matricula) throws IllegalArgumentException {
        if (matricula.getUsuarioId() <= 0) {
            throw new IllegalArgumentException("O ID do usuário é inválido!");
        }
        if (matricula.getCursoId() <= 0) {
            throw new IllegalArgumentException("O ID do curso é inválido!");
        }
        if (matricula.getStatus() == null || matricula.getStatus().isEmpty()) {
            throw new IllegalArgumentException("O status da matrícula é obrigatório!");
        }
    }
}