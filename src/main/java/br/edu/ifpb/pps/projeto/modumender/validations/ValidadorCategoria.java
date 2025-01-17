package br.edu.ifpb.pps.projeto.modumender.validations;

import br.edu.ifpb.pps.projeto.modumender.models.Categoria;

public class ValidadorCategoria implements Validador<Categoria> {

    @Override
    public void validar(Categoria categoria) throws IllegalArgumentException {
        if (categoria.getNome() == null || categoria.getNome().isEmpty()) {
            throw new IllegalArgumentException("O nome da categoria é obrigatório!");
        }
        // Descrição é opcional, então não validamos
    }
}
