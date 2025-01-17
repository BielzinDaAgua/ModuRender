package br.edu.ifpb.pps.projeto.modumender.validations;

import br.edu.ifpb.pps.projeto.modumender.models.Certificado;

public class ValidadorCertificado implements Validador<Certificado> {
    @Override
    public void validar(Certificado certificado) throws IllegalArgumentException {
        if (certificado.getUsuarioId() <= 0) {
            throw new IllegalArgumentException("O ID do usuário é inválido!");
        }
        if (certificado.getCursoId() <= 0) {
            throw new IllegalArgumentException("O ID do curso é inválido!");
        }
    }
}