package br.edu.ifpb.pps.projeto.modumender.main;


import br.edu.ifpb.pps.projeto.modumender.ModuRender;
import br.edu.ifpb.pps.projeto.modumender.models.Usuario;
import br.edu.ifpb.pps.projeto.modumender.models.Curso;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;

public class ModuRenderApp {
    public static void main(String[] args) {
        try {
            // Criar um usuário
            Usuario usuario = new Usuario();
            usuario.setNome("Carlos Silva");
            usuario.setEmail("carlos@email.com");
            usuario.setSenha("senha123");
            usuario.setTipoUsuario("INSTRUTOR");
            ModuRender.save(usuario); // Salva o usuário

            // Criar um curso
            Curso curso = new Curso();
            curso.setTitulo("Curso de Java Avançado");
            curso.setDescricao("Aprenda recursos avançados do Java.");
            curso.setPreco(149.99);
            curso.setDataCriacao(Date.valueOf(LocalDate.now().toString()));
            curso.setInstrutorId(usuario.getId());
            ModuRender.save(curso); // Salva o curso

            // Listar todos os usuários
            ModuRender.findAll(Usuario.class).forEach(System.out::println);

            // Buscar um curso pelo ID
            Curso cursoBuscado = ModuRender.findById(Curso.class, curso.getId());
            System.out.println("Curso encontrado: " + cursoBuscado);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}