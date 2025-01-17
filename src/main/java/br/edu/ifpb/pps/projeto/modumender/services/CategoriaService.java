package br.edu.ifpb.pps.projeto.modumender.services;

import br.edu.ifpb.pps.projeto.modumender.dao.CategoriaDAO;
import br.edu.ifpb.pps.projeto.modumender.models.Categoria;
import br.edu.ifpb.pps.projeto.modumender.validations.ValidadorCategoria;

import java.sql.SQLException;
import java.util.List;

public class CategoriaService {
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();
    private final ValidadorCategoria validadorCategoria = new ValidadorCategoria();

    public void salvarCategoria(Categoria categoria) throws SQLException {
        validadorCategoria.validar(categoria); // Valida a entidade
        categoriaDAO.save(categoria);         // Salva no banco
    }

    public List<Categoria> listarCategorias() throws SQLException {
        return categoriaDAO.findAll(); // Retorna todas as categorias do banco
    }

    public Categoria buscarCategoriaPorId(int id) throws SQLException {
        return categoriaDAO.findById(id); // Retorna uma categoria específica pelo ID
    }
}
