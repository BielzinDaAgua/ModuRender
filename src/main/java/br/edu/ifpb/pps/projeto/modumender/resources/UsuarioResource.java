package br.edu.ifpb.pps.projeto.modumender.resources;

import br.edu.ifpb.pps.projeto.modumender.annotations.CrudResource;
import br.edu.ifpb.pps.projeto.modumender.model.Usuario;

/**
 * @CrudResource gera rotas automáticas de CRUD em "/usuarios"
 */
@CrudResource(path="/usuarios", entity=Usuario.class)
public class UsuarioResource {
    // Pode estar vazio.
    // O framework cria GET/POST/PUT/DELETE rotas p/ /usuarios ...
}
