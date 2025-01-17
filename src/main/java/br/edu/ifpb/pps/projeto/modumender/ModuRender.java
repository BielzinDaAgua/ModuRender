package br.edu.ifpb.pps.projeto.modumender;

import br.edu.ifpb.pps.projeto.modumender.services.*;
import br.edu.ifpb.pps.projeto.modumender.models.*;
import br.edu.ifpb.pps.projeto.modumender.util.MigrationManager;

import java.sql.SQLException;
import java.util.List;

public class ModuRender {
    // Serviços
    private static final UsuarioService usuarioService = new UsuarioService();
    private static final CursoService cursoService = new CursoService();
    private static final MatriculaService matriculaService = new MatriculaService();
    private static final AvaliacaoService avaliacaoService = new AvaliacaoService();
    private static final CertificadoService certificadoService = new CertificadoService();
    private static final CategoriaService categoriaService = new CategoriaService();

    static {
        // Criar as tabelas automaticamente ao carregar o framework
        MigrationManager.criarTabelas();
    }


    // Método para salvar entidades no banco
    public static <T> void save(T entity) throws SQLException {
        if (entity instanceof Usuario) {
            usuarioService.salvarUsuario((Usuario) entity);
        } else if (entity instanceof Curso) {
            cursoService.salvarCurso((Curso) entity);
        } else if (entity instanceof Matricula) {
            matriculaService.salvarMatricula((Matricula) entity);
        } else if (entity instanceof Avaliacao) {
            avaliacaoService.salvarAvaliacao((Avaliacao) entity);
        } else if (entity instanceof Certificado) {
            certificadoService.salvarCertificado((Certificado) entity);
        } else if (entity instanceof Categoria) { // Novo suporte para Categoria
            categoriaService.salvarCategoria((Categoria) entity);
        } else {
            throw new IllegalArgumentException("Entidade não reconhecida pelo framework.");
        }
    }

    // Método para buscar uma entidade por ID
    public static <T> T findById(Class<T> clazz, int id) throws SQLException {
        if (clazz == Usuario.class) {
            return clazz.cast(usuarioService.buscarUsuarioPorId(id));
        } else if (clazz == Curso.class) {
            return clazz.cast(cursoService.listarCursos().stream()
                    .filter(c -> c.getId() == id).findFirst().orElse(null));
        } else {
            throw new IllegalArgumentException("Classe não suportada pelo framework.");
        }
    }

    // Método para listar todas as entidades de um tipo
    @SuppressWarnings("unchecked")
    public static <T> List<T> findAll(Class<T> clazz) throws SQLException {
        if (clazz == Usuario.class) {
            // Casting explícito para o tipo genérico T
            return (List<T>) usuarioService.buscarTodosUsuarios();
        } else if (clazz == Curso.class) {
            return (List<T>) cursoService.listarCursos();
        } else {
            throw new IllegalArgumentException("Classe não suportada pelo framework.");
        }
    }
}