package br.edu.ifpb.pps.projeto.modumender.servidor;

import br.edu.ifpb.pps.projeto.modumender.controller.RouteDefinition;
import br.edu.ifpb.pps.projeto.modumender.controller.ControllerHandler;
import br.edu.ifpb.pps.projeto.modumender.controller.ControllerScanner;
import br.edu.ifpb.pps.projeto.modumender.crud.CrudScanner;
import br.edu.ifpb.pps.projeto.modumender.crud.CrudHandler;
import br.edu.ifpb.pps.projeto.modumender.controller.ControllerHandlerFunctional;
import br.edu.ifpb.pps.projeto.modumender.controller.RouteDefinition;
import br.edu.ifpb.pps.projeto.modumender.crud.CrudResourceDefinition;
import br.edu.ifpb.pps.projeto.modumender.http.HttpRequest;
import br.edu.ifpb.pps.projeto.modumender.http.HttpResponse;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Servlet principal. Ao iniciar, combina rotas
 * manuais (ControllerScanner) e
 * as automáticas (CrudScanner).
 *
 * Em "service(...)" faz matching placeholder.
 */
public class FrameworkServlet extends HttpServlet {

    private static final List<RouteDefinition> routeDefinitions = new ArrayList<>();

    @Override
    public void init() {
        System.out.println("FrameworkServlet init");

        // 🔹 Escanear controladores manuais
        ControllerScanner.scanControllers("br.edu.ifpb.pps.projeto.modumender.controller");

        // 🔹 Adicionar rotas manuais ao servlet
        routeDefinitions.addAll(ControllerScanner.getRouteDefinitions());

        // 🔹 Escanear recursos CRUD
        CrudScanner.scanCrudResources("br.edu.ifpb.pps.projeto.modumender.resources");

        // 🔹 Gerar rotas CRUD automáticas
        for (CrudResourceDefinition def : CrudScanner.getDefinitions()) {
            String base = def.getBasePath();
            var handler = new CrudHandler(def.getEntityClass());

            // Registra as rotas do CRUD
            routeDefinitions.add(new RouteDefinition("GET", base, new ControllerHandlerFunctional(handler::listAll)));
            routeDefinitions.add(new RouteDefinition("GET", base + "/{id}", new ControllerHandlerFunctional(handler::findById)));
            routeDefinitions.add(new RouteDefinition("POST", base, new ControllerHandlerFunctional(handler::create)));
            routeDefinitions.add(new RouteDefinition("PUT", base + "/{id}", new ControllerHandlerFunctional(handler::update)));
            routeDefinitions.add(new RouteDefinition("DELETE", base + "/{id}", new ControllerHandlerFunctional(handler::delete)));

            // ✅ Adicione logs para depurar
            System.out.println("🔄 CRUD rotas registradas:");
            System.out.println("   ➡ GET " + base);
            System.out.println("   ➡ GET " + base + "/{id}");
            System.out.println("   ➡ POST " + base);
            System.out.println("   ➡ PUT " + base + "/{id}");
            System.out.println("   ➡ DELETE " + base + "/{id}");
        }

        // 🔹 Exibir todas as rotas finais registradas
        System.out.println("📌 Rotas finais no FrameworkServlet:");
        for (RouteDefinition rd : routeDefinitions) {
            System.out.println("   ➡ " + rd.getHttpMethod() + " " + rd.getPathTemplate());
        }
    }



    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String method = req.getMethod();
        String path   = req.getRequestURI();

        System.out.println("📢 Requisição recebida: " + method + " " + path);


        HttpRequest  request  = new HttpRequest(req);
        HttpResponse response = new HttpResponse(resp);

        // Tenta achar match com placeholders
        System.out.println("🔍 Buscando handler para: " + method + " " + path);
        ControllerHandler handler = findMatchingHandler(method, path, request);
        System.out.println("🛠 Handler encontrado: " + (handler != null ? handler.getClass().getSimpleName() : "null"));

        if (handler == null) {
            resp.setStatus(404);
            resp.getWriter().write("Rota não encontrada: " + path);
            return;
        }

        try {
            Object result = handler.invoke(request, response);
            if (result instanceof String) {
                response.writeBody((String) result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
            resp.getWriter().write("Erro interno: " + e.getMessage());
        }
    }

    private ControllerHandler findMatchingHandler(String method, String path, HttpRequest req) {
        System.out.println("🗺 Rotas registradas:");
        for (RouteDefinition rd : routeDefinitions) {
            System.out.println("   ➡ " + rd.getHttpMethod() + ":" + rd.getPathTemplate());
        }

        for (RouteDefinition rd : routeDefinitions) {
            if (!rd.getHttpMethod().equalsIgnoreCase(method)) continue;
            System.out.println("🔍 Comparando " + rd.getPathTemplate() + " com " + path);

            var matchResult = matchTemplate(rd.getPathTemplate(), path);
            if (matchResult != null) {
                matchResult.forEach(req::setPathParam);
                return rd.getHandler();
            }
        }
        return null;
    }

    public ControllerHandler testFindHandler(String method, String path) {
        HttpRequest dummyRequest = new HttpRequest(null);
        return findMatchingHandler(method, path, dummyRequest);
    }





    // match placeholders
    private java.util.Map<String, String> matchTemplate(String template, String actual) {
        System.out.println("🔍 Comparando template '" + template + "' com caminho '" + actual + "'");

        String[] tParts = template.split("/");
        String[] aParts = actual.split("/");

        if (tParts.length != aParts.length) {
            System.out.println("❌ Falha: Diferente número de segmentos (" + tParts.length + " vs " + aParts.length + ")");
            return null;
        }

        java.util.Map<String, String> params = new java.util.HashMap<>();
        for (int i = 0; i < tParts.length; i++) {
            String tp = tParts[i];
            String ap = aParts[i];

            System.out.println("  📌 Comparando segmento [" + tp + "] com [" + ap + "]");

            if (tp.startsWith("{") && tp.endsWith("}")) {
                params.put(tp.substring(1, tp.length() - 1), ap);
            } else if (!tp.equals(ap)) {
                System.out.println("❌ Falha: Segmento diferente (" + tp + " != " + ap + ")");
                return null;
            }
        }

        System.out.println("✅ Match bem-sucedido!");
        return params;
    }


}
