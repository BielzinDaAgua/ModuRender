package br.edu.ifpb.pps.projeto.modumender.template;

import java.util.Map;

public interface ResponseStrategy {
    /**
     * Gera a resposta final (HTML, JSON, etc.) a partir do "template"
     * e do model de dados.
     */
    String generateResponse(TemplateAutoDefinition def, Map<String, Object> model) throws Exception;
}
