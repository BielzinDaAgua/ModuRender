package br.edu.ifpb.pps.projeto.modumender.template;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * Gera saída em JSON.
 */
public class JsonResponseStrategy implements ResponseStrategy {

    @Override
    public String generateResponse(TemplateAutoDefinition def, Map<String, Object> model) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(model);
    }
}
