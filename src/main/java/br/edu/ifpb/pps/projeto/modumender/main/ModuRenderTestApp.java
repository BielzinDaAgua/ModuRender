package br.edu.ifpb.pps.projeto.modumender.main;

import br.edu.ifpb.pps.projeto.modumender.ModuRender;
import br.edu.ifpb.pps.projeto.modumender.models.*;
import br.edu.ifpb.pps.projeto.modumender.dao.*;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ModuRenderTestApp {
    public static void main(String[] args) {
        try {
            // ==============================
            // TESTE 1: Criar e salvar vários usuários
            // ==============================
            System.out.println("=== TESTE 1: Criar e salvar vários usuários ===");
            Usuario instrutor1 = new Usuario();
            instrutor1.setNome("João Silva");
            instrutor1.setEmail("joao.silva@email.com");
            instrutor1.setSenha("senha123");
            instrutor1.setTipoUsuario("INSTRUTOR");
            ModuRender.save(instrutor1);
            System.out.println("Usuário salvo: " + instrutor1);

            Usuario aluno1 = new Usuario();
            aluno1.setNome("Maria Souza");
            aluno1.setEmail("maria.souza@email.com");
            aluno1.setSenha("senha456");
            aluno1.setTipoUsuario("ALUNO");
            ModuRender.save(aluno1);
            System.out.println("Usuário salvo: " + aluno1);

            Usuario aluno2 = new Usuario();
            aluno2.setNome("Carlos Pereira");
            aluno2.setEmail("carlos.pereira@email.com");
            aluno2.setSenha("senha789");
            aluno2.setTipoUsuario("ALUNO");
            ModuRender.save(aluno2);
            System.out.println("Usuário salvo: " + aluno2);

            // ==============================
            // TESTE 2: Criar e salvar vários cursos
            // ==============================
            System.out.println("\n=== TESTE 2: Criar e salvar vários cursos ===");
            Curso curso1 = new Curso();
            curso1.setTitulo("Curso de Java Básico");
            curso1.setDescricao("Aprenda Java do zero com exercícios práticos.");
            curso1.setPreco(99.99);
            curso1.setDataCriacao(Date.valueOf(LocalDate.now()));
            curso1.setInstrutorId(instrutor1.getId());
            ModuRender.save(curso1);
            System.out.println("Curso salvo: " + curso1);

            Curso curso2 = new Curso();
            curso2.setTitulo("Curso de Python para Análise de Dados");
            curso2.setDescricao("Aprenda Python com foco em análise de dados e machine learning.");
            curso2.setPreco(149.99);
            curso2.setDataCriacao(Date.valueOf(LocalDate.now()));
            curso2.setInstrutorId(instrutor1.getId());
            ModuRender.save(curso2);
            System.out.println("Curso salvo: " + curso2);

            // ==============================
            // TESTE 3: Criar e salvar várias categorias
            // ==============================
            System.out.println("\n=== TESTE 3: Criar e salvar várias categorias ===");
            Categoria categoria1 = new Categoria();
            categoria1.setNome("Programação");
            categoria1.setDescricao("Cursos relacionados à programação.");
            ModuRender.save(categoria1);
            System.out.println("Categoria salva: " + categoria1);

            Categoria categoria2 = new Categoria();
            categoria2.setNome("Ciência de Dados");
            categoria2.setDescricao("Cursos relacionados à análise e ciência de dados.");
            ModuRender.save(categoria2);
            System.out.println("Categoria salva: " + categoria2);

            // ==============================
            // TESTE 4: Criar e salvar várias matrículas
            // ==============================
            System.out.println("\n=== TESTE 4: Criar e salvar várias matrículas ===");
            Matricula matricula1 = new Matricula();
            matricula1.setUsuarioId(aluno1.getId());
            matricula1.setCursoId(curso1.getId());
            matricula1.setDataMatricula(Date.valueOf(LocalDate.now()));
            matricula1.setStatus("ATIVO");
            ModuRender.save(matricula1);
            System.out.println("Matrícula salva: " + matricula1);

            Matricula matricula2 = new Matricula();
            matricula2.setUsuarioId(aluno2.getId());
            matricula2.setCursoId(curso1.getId());
            matricula2.setDataMatricula(Date.valueOf(LocalDate.now()));
            matricula2.setStatus("ATIVO");
            ModuRender.save(matricula2);
            System.out.println("Matrícula salva: " + matricula2);

            // ==============================
            // TESTE 5: Criar e salvar várias avaliações
            // ==============================
            System.out.println("\n=== TESTE 5: Criar e salvar várias avaliações ===");
            Avaliacao avaliacao1 = new Avaliacao();
            avaliacao1.setUsuarioId(aluno1.getId());
            avaliacao1.setCursoId(curso1.getId());
            avaliacao1.setNota(5);
            avaliacao1.setComentario("Excelente curso, muito didático!");
            avaliacao1.setDataAvaliacao(Date.valueOf(LocalDate.now()));
            ModuRender.save(avaliacao1);
            System.out.println("Avaliação salva: " + avaliacao1);

            Avaliacao avaliacao2 = new Avaliacao();
            avaliacao2.setUsuarioId(aluno2.getId());
            avaliacao2.setCursoId(curso1.getId());
            avaliacao2.setNota(4);
            avaliacao2.setComentario("Curso muito bom, mas poderia ter mais exemplos.");
            avaliacao2.setDataAvaliacao(Date.valueOf(LocalDate.now()));
            ModuRender.save(avaliacao2);
            System.out.println("Avaliação salva: " + avaliacao2);

            // ==============================
            // TESTE 6: Listar todos os usuários
            // ==============================
            System.out.println("\n=== TESTE 6: Listar todos os usuários ===");
            ModuRender.findAll(Usuario.class).forEach(System.out::println);

            // ==============================
            // TESTE 7: Listar todos os cursos
            // ==============================
            System.out.println("\n=== TESTE 7: Listar todos os cursos ===");
            ModuRender.findAll(Curso.class).forEach(System.out::println);

            // ==============================
            // TESTE 8: Buscar um curso pelo ID
            // ==============================
            System.out.println("\n=== TESTE 8: Buscar um curso pelo ID ===");
            Curso cursoBuscado = ModuRender.findById(Curso.class, curso1.getId());
            System.out.println("Curso encontrado: " + cursoBuscado);

            // ==============================
            // TESTE 9: Consultar cursos por categoria
            // ==============================
            System.out.println("\n=== TESTE 9: Consultar cursos por categoria ===");
            CursoDAO cursoDAO = new CursoDAO();
            List<Curso> cursosPorCategoria = cursoDAO.findByCategoria("Programação");
            cursosPorCategoria.forEach(System.out::println);

            // ==============================
            // TESTE 10: Listar alunos matriculados em um curso
            // ==============================
            System.out.println("\n=== TESTE 10: Listar alunos matriculados em um curso ===");
            MatriculaDAO matriculaDAO = new MatriculaDAO();
            List<Usuario> alunosMatriculados = matriculaDAO.findAlunosByCursoId(curso1.getId());
            alunosMatriculados.forEach(System.out::println);

            // ==============================
            // TESTE 11: Listar avaliações de um curso ordenadas por nota
            // ==============================
            System.out.println("\n=== TESTE 11: Listar avaliações de um curso ordenadas por nota ===");
            AvaliacaoDAO avaliacaoDAO = new AvaliacaoDAO();
            List<Avaliacao> avaliacoesOrdenadas = avaliacaoDAO.findAvaliacoesByCursoId(curso1.getId());
            avaliacoesOrdenadas.forEach(System.out::println);

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erro ao executar os testes do ModuRender.");
        }
    }
}
