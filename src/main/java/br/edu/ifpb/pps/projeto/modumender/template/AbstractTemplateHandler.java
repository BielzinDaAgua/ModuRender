package br.edu.ifpb.pps.projeto.modumender.template;

import br.edu.ifpb.pps.projeto.modumender.http.HttpRequest;

import java.util.Map;

/**
 * Define o "esqueleto" (template method) para lidar com requisições de template.
 * Subclasses só implementam os passos específicos (hooks).
 */
public abstract class AbstractTemplateHandler {

    // Template Method: sequência final de passos para lidar com a rota de template
    public final String handleTemplateRequest(String path, HttpRequest request) {
        // Passo 1: Obter a definição da rota (se existir)
        TemplateAutoDefinition def = findTemplateDefinition(path);
        if (def == null) {
            return handleNoDefinitionFound(path, request);
        }

        // Passo 2: Montar o model (tentando invocar buildModel)
        Map<String, Object> model = buildModel(def);

        // Passo 3: Renderizar (a forma de renderizar fica a cargo da subclasse ou de um helper)
        return renderTemplate(def, model, request);
    }

    // "Hook" 1: como localizar a definition
    protected abstract TemplateAutoDefinition findTemplateDefinition(String path);

    // "Hook" 2: como tratar se não houver definition
    protected abstract String handleNoDefinitionFound(String path, HttpRequest request);

    // "Hook" 3: como montar o model (invocar buildModel, etc.)
    protected Map<String, Object> buildModel(TemplateAutoDefinition def) {
        return TemplateUtils.invokeBuildModel(def); // veremos abaixo
    }

    // "Hook" 4: como renderizar
    protected abstract String renderTemplate(TemplateAutoDefinition def, Map<String, Object> model, HttpRequest request);
}