package br.edu.ifpb.pps.projeto.modumender.template;

import br.edu.ifpb.pps.projeto.modumender.http.HttpRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Classe concreta que estende AbstractTemplateHandler (Template Method)
 * e, internamente, utiliza "Strategy" para escolher o tipo de resposta (JSON, HTML).
 */
public class TemplateRouteHandler extends AbstractTemplateHandler {

    // Mapa manual (opcional):
    private static final Map<String, Function<HttpRequest, String>> templateRoutes = new HashMap<>();

    /**
     * Método estático para quem chamar este handler diretamente.
     */
    public static String handleRequest(String path, HttpRequest request) {
        TemplateRouteHandler handler = new TemplateRouteHandler();
        return handler.handleTemplateRequest(path, request);
    }

    /**
     * Hook 1: encontrar a definição (usamos a classe TemplateRouteScanner).
     */
    @Override
    protected TemplateAutoDefinition findTemplateDefinition(String path) {
        return TemplateRouteScanner.getDefinition(path);
    }

    /**
     * Hook 2: se não encontrar uma rota, verificamos as rotas manuais.
     */
    @Override
    protected String handleNoDefinitionFound(String path, HttpRequest request) {
        var manual = templateRoutes.get(path);
        if (manual != null) {
            return manual.apply(request);
        }
        return null; // não achou
    }

    /**
     * Hook 4: agora usando Strategy para gerar a saída (HTML ou JSON).
     */
    @Override
    protected String renderTemplate(TemplateAutoDefinition def, Map<String, Object> model, HttpRequest request) {
        try {
            // 1) Escolher a Strategy
            String acceptHeader = request.getHeader("Accept");
            ResponseStrategy strategy = selectStrategy(acceptHeader);

            // 2) Gerar resposta com a Strategy
            return strategy.generateResponse(def, model);
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\":\"Falha ao gerar resposta.\"}";
        }
    }

    /**
     * Método auxiliar para escolher a Strategy de saída (JSON ou HTML).
     * Você pode adicionar mais formatos se quiser.
     */
    private ResponseStrategy selectStrategy(String acceptHeader) {
        if (acceptHeader != null && acceptHeader.contains("application/json")) {
            // Se o header Accept contém "application/json", escolhemos JSON
            return new JsonResponseStrategy();
        }
        // Caso contrário, HTML
        return new HtmlResponseStrategy();
    }
}
