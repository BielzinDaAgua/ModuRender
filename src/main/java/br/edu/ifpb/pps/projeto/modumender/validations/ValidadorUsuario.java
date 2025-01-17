package br.edu.ifpb.pps.projeto.modumender.validations;

import br.edu.ifpb.pps.projeto.modumender.models.Usuario;

public class ValidadorUsuario implements Validador<Usuario> {
    @Override
    public void validar(Usuario usuario) throws IllegalArgumentException {
        if (usuario.getNome() == null || usuario.getNome().isEmpty()) {
            throw new IllegalArgumentException("O nome é obrigatório!");
        }
        if (usuario.getEmail() == null || !usuario.getEmail().matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
            throw new IllegalArgumentException("E-mail inválido!");
        }
        if (usuario.getSenha() == null || usuario.getSenha().length() < 6) {
            throw new IllegalArgumentException("A senha deve ter pelo menos 6 caracteres!");
        }
    }
}