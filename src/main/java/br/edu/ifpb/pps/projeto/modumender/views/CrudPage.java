package br.edu.ifpb.pps.projeto.modumender.views;


import br.edu.ifpb.pps.projeto.modumender.annotations.TemplateRoute;
import br.edu.ifpb.pps.projeto.modumender.dao.DAOFactory;
import br.edu.ifpb.pps.projeto.modumender.dao.GenericDAO;
import br.edu.ifpb.pps.projeto.modumender.model.Usuario;
import br.edu.ifpb.pps.projeto.modumender.template.TemplateRenderer;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@TemplateRoute(path = "/crud", template = "crud/crud")
public class CrudPage {


    public String renderPage() throws Exception {
        GenericDAO<Usuario> usuarioDao = DAOFactory.createDAO(Usuario.class);
        List<Usuario> usuarios = usuarioDao.findAll();

        return TemplateRenderer.render("src/main/resources/templates/crud.html", (Map<String, Object>) usuarios);
    }
}
