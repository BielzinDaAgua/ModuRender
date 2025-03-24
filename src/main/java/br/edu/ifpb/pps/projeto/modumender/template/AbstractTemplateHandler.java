package br.edu.ifpb.pps.projeto.modumender.template;

import br.edu.ifpb.pps.projeto.modumender.http.HttpRequest;

import java.util.Map;

/**
 * Define o "esqueleto" (template method) para lidar com requisições de template.
 * Subclasses só implementam os passos específicos (hooks).
 */

/**
 É uma classe abstrata que define o Template Method handleTemplateRequest.
 Esse método estabelece a ordem em que as operações acontecem ao responder requisições.
 Possui quatro "hooks" (métodos abstratos que devem ser implementados pelas subclasses):

 Fluxo básico (handleTemplateRequest):
 Encontrar definição - Gerar modelo - Renderizar saída.
 **/
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

    // "Hook" 1: Localizar se há definição associada à rota requisitada
    protected abstract TemplateAutoDefinition findTemplateDefinition(String path);

    // "Hook" 2: Ação tomada se não houver definição encontrada
    protected abstract String handleNoDefinitionFound(String path, HttpRequest request);

    // "Hook" 3: como montar o model de dados (invocar buildModel, etc.)
    protected Map<String, Object> buildModel(TemplateAutoDefinition def) {
        return TemplateUtils.invokeBuildModel(def); // veremos abaixo
    }

    // "Hook" 4: Renderizar a resposta final
    protected abstract String renderTemplate(TemplateAutoDefinition def, Map<String, Object> model, HttpRequest request);
}