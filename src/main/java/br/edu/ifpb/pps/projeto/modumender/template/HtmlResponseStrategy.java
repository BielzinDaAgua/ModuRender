package br.edu.ifpb.pps.projeto.modumender.template;

import java.util.Map;

/**
 * Gera saída em HTML via TemplateRenderer.
 */
public class HtmlResponseStrategy implements ResponseStrategy {

    @Override
    public String generateResponse(TemplateAutoDefinition def, Map<String, Object> model) throws Exception {
        // Renderiza usando TemplateRenderer
        return TemplateRenderer.render(def.getTemplateName(), model);
    }
}
