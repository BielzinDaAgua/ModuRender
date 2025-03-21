package br.edu.ifpb.pps.projeto.modumender.template;

import br.edu.ifpb.pps.projeto.modumender.http.HttpRequest;
import java.lang.reflect.Method;
import java.util.*;

public class TemplateRouteHandler {

    // Mapa manual (opcional):
    private static final Map<String, java.util.function.Function<HttpRequest, String>> templateRoutes = new HashMap<>();

    static {
        // rotas manuais, se quiser
    }

    public static String handleRequest(String path, HttpRequest request) {
        // 1) Verifica se há route automática
        TemplateAutoDefinition autoDef = TemplateRouteScanner.getDefinition(path);
        if (autoDef != null) {
            // Tenta invocar buildModel() da classe, se existir
            Map<String,Object> model = Map.of(); // default

            try {
                Class<?> cls = autoDef.getSourceClass();
                // Procura um método public static Map<String,Object> buildModel()
                Method m = cls.getMethod("buildModel", (Class<?>[]) null);

                // Se achou o método, invoca
                if (m.getReturnType().equals(Map.class)) {
                    // Invoca método estático
                    @SuppressWarnings("unchecked")
                    Map<String,Object> res = (Map<String,Object>) m.invoke(null);
                    model = res != null ? res : Map.of();
                }
            } catch (NoSuchMethodException e) {
                // Não tem buildModel(), então model = Map.of()
            } catch (Exception e) {
                e.printStackTrace();
                return "Erro ao invocar buildModel: " + e.getMessage();
            }

            // Renderiza
            return TemplateRenderer.render(autoDef.getTemplateName(), model);
        }

        // 2) Se não tiver route auto, verifica rotas manuais
        var handler = templateRoutes.get(path);
        if (handler != null) {
            return handler.apply(request);
        }

        // 3) Se não achar, null
        return null;
    }
}
