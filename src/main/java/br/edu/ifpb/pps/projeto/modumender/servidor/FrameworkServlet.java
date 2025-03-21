package br.edu.ifpb.pps.projeto.modumender.servidor;

import br.edu.ifpb.pps.projeto.modumender.controller.ControllerHandlerFunctional;
import br.edu.ifpb.pps.projeto.modumender.controller.RouteDefinition;
import br.edu.ifpb.pps.projeto.modumender.controller.ControllerHandler;
import br.edu.ifpb.pps.projeto.modumender.controller.ControllerScanner;
import br.edu.ifpb.pps.projeto.modumender.crud.CrudScanner;
import br.edu.ifpb.pps.projeto.modumender.crud.CrudHandler;
import br.edu.ifpb.pps.projeto.modumender.crud.CrudResourceDefinition;
import br.edu.ifpb.pps.projeto.modumender.http.HttpRequest;
import br.edu.ifpb.pps.projeto.modumender.http.HttpResponse;
import br.edu.ifpb.pps.projeto.modumender.template.TemplateRouteHandler;
import br.edu.ifpb.pps.projeto.modumender.annotations.RestController;
import br.edu.ifpb.pps.projeto.modumender.rest.JsonUtil;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Servlet principal. Agora, também integra o TemplateRouteHandler.
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

            routeDefinitions.add(new RouteDefinition("GET", base,
                    new ControllerHandlerFunctional((req, resp) -> handler.listAll(req, resp))));

            routeDefinitions.add(new RouteDefinition("GET", base + "/{id}",
                    new ControllerHandlerFunctional((req, resp) -> handler.findById(req, resp))));

            routeDefinitions.add(new RouteDefinition("POST", base,
                    new ControllerHandlerFunctional((req, resp) -> handler.create(req, resp))));

            routeDefinitions.add(new RouteDefinition("PUT", base + "/{id}",
                    new ControllerHandlerFunctional((req, resp) -> handler.update(req, resp))));

            routeDefinitions.add(new RouteDefinition("DELETE", base + "/{id}",
                    new ControllerHandlerFunctional((req, resp) -> handler.delete(req, resp))));

            System.out.println("🔄 CRUD rotas registradas: " + base);
        }

        System.out.println("📌 Rotas finais registradas:");
        for (RouteDefinition rd : routeDefinitions) {
            System.out.println("   ➡ " + rd.getHttpMethod() + " " + rd.getPathTemplate());
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        // 1) Instanciar e chamar manualmente
        CookieAuthFilter authChecker = new CookieAuthFilter();
        boolean intercepted = authChecker.doAuthCheck(req, resp);
        if (intercepted) {
            // se doAuthCheck retornar true,
            // a requisição foi bloqueada (401).
            // então paramos aqui sem prosseguir
            return;
        }

        // 2) Prosseguir com a lógica normal do FrameworkServlet
        String method = req.getMethod();
        String path   = req.getRequestURI();

        HttpRequest  request  = new HttpRequest(req);
        HttpResponse response = new HttpResponse(resp);

        // Tenta TemplateRouteHandler
        String renderedTemplate = TemplateRouteHandler.handleRequest(path, request);
        if (renderedTemplate != null) {
            response.writeBody(renderedTemplate);
            return;
        }

        // Tenta achar rota manual
        ControllerHandler handler = findMatchingHandler(method, path, request);
        if (handler == null) {
            resp.setStatus(404);
            resp.getWriter().write("Rota não encontrada: " + path);
            return;
        }

        try {
            Object result = handler.invoke(request, response);
            if (result == null) {
                resp.setStatus(204); // No Content
                return;
            }

            // Se for String e não rest, mandamos texto normal
            if (result instanceof String && !isRestController(handler)) {
                response.writeBody((String) result);
            }
            // Se for de um @RestController e não String, serializamos em JSON
            else if (isRestController(handler)) {
                try {
                    resp.setContentType("application/json");
                    String json = JsonUtil.toJson(result);
                    response.writeBody(json);
                } catch (Exception e) {
                    e.printStackTrace();
                    resp.setStatus(500);
                    response.writeBody("Erro ao gerar JSON: " + e.getMessage());
                }
            }
            else {
                // fallback: se for string mas rest controller => mandar text
                // ou se for objeto mas controller => .toString()?
                response.writeBody(result.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
            resp.getWriter().write("Erro interno: " + e.getMessage());
        }
    }

    private boolean isRestController(ControllerHandler handler) {
        Class<?> ctrlClass = handler.getControllerClass();
        if (ctrlClass == null) return false;
        return ctrlClass.isAnnotationPresent(RestController.class);
    }


    private ControllerHandler findMatchingHandler(String method, String path, HttpRequest req) {
        for (RouteDefinition rd : routeDefinitions) {
            if (!rd.getHttpMethod().equalsIgnoreCase(method)) continue;

            var matchResult = matchTemplate(rd.getPathTemplate(), path);
            if (matchResult != null) {
                matchResult.forEach(req::setPathParam);
                return rd.getHandler();
            }
        }
        return null;
    }

    private java.util.Map<String, String> matchTemplate(String template, String actual) {
        String[] tParts = template.split("/");
        String[] aParts = actual.split("/");

        if (tParts.length != aParts.length) {
            return null;
        }

        java.util.Map<String, String> params = new java.util.HashMap<>();
        for (int i = 0; i < tParts.length; i++) {
            String tp = tParts[i];
            String ap = aParts[i];

            if (tp.startsWith("{") && tp.endsWith("}")) {
                params.put(tp.substring(1, tp.length() - 1), ap);
            } else if (!tp.equals(ap)) {
                return null;
            }
        }
        return params;
    }
}
