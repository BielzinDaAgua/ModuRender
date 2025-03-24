package br.edu.ifpb.pps.projeto.modumender.template;

import java.util.Map;

public interface ResponseStrategy {
    /**
     * Gera a resposta final (HTML, JSON, etc.) a partir do "template"
     * e do model de dados.
     */


    /**
     ResponseStrategy: interface para estratégias de resposta.

     HtmlResponseStrategy: gera HTML usando TemplateRenderer (Thymeleaf).

     JsonResponseStrategy: gera JSON usando Jackson.
     */
    String generateResponse(TemplateAutoDefinition def, Map<String, Object> model) throws Exception;
}
