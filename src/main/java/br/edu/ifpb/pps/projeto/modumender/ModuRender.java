package br.edu.ifpb.pps.projeto.modumender;

import br.edu.ifpb.pps.projeto.modumender.services.*;
import br.edu.ifpb.pps.projeto.modumender.models.*;
import br.edu.ifpb.pps.projeto.modumender.util.ConexaoDB;
import br.edu.ifpb.pps.projeto.modumender.util.MigrationManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    // Método para buscar entidades com filtros, ordenação e joins //Não usado ainda.
    public static <T> List<T> findWithFilters(Class<T> entityClass,
                                              Map<String, Object> filters,
                                              String orderBy,
                                              boolean ascending,
                                              String joinClause) throws SQLException {
        List<T> results = new ArrayList<>();
        String tableName = entityClass.getSimpleName().toLowerCase() + "s"; // Nome da tabela
        StringBuilder query = new StringBuilder("SELECT * FROM " + tableName);

        // Adicionar cláusula JOIN, se especificada
        if (joinClause != null && !joinClause.isBlank()) {
            query.append(" ").append(joinClause);
        }

        // Adicionar cláusula WHERE com filtros
        if (filters != null && !filters.isEmpty()) {
            query.append(" WHERE ");
            filters.forEach((key, value) -> query.append(key).append(" = ? AND "));
            query.setLength(query.length() - 4); // Remover o último "AND"
        }

        // Adicionar cláusula ORDER BY
        if (orderBy != null && !orderBy.isBlank()) {
            query.append(" ORDER BY ").append(orderBy);
            query.append(ascending ? " ASC" : " DESC");
        }

        System.out.println("Query Gerada: " + query);

        try (Connection conn = ConexaoDB.getInstance();
             PreparedStatement stmt = conn.prepareStatement(query.toString())) {

            // Preencher os parâmetros da query
            if (filters != null && !filters.isEmpty()) {
                int index = 1;
                for (Object value : filters.values()) {
                    ((java.sql.PreparedStatement) stmt).setObject(index++, value);
                }
            }

            // Executar a consulta
            ResultSet rs = stmt.executeQuery();

            // Processar os resultados
            while (rs.next()) {
                T entity = entityClass.getDeclaredConstructor().newInstance();
                ResultSetMetaData metaData = rs.getMetaData();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    String columnName = metaData.getColumnName(i);
                    Object columnValue = rs.getObject(i);

                    // Usar reflection para setar valores nos atributos da entidade
                    var field = entityClass.getDeclaredField(columnName);
                    field.setAccessible(true);
                    field.set(entity, columnValue);
                }
                results.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }
}