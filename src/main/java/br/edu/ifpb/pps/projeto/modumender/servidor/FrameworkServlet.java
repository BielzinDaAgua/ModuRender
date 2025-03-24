package br.edu.ifpb.pps.projeto.modumender.servidor;

import br.edu.ifpb.pps.projeto.modumender.controller.ControllerHandlerFunctional;
import br.edu.ifpb.pps.projeto.modumender.controller.RouteDefinition;
import br.edu.ifpb.pps.projeto.modumender.controller.ControllerHandler;
import br.edu.ifpb.pps.projeto.modumender.controller.ControllerScanner;
import br.edu.ifpb.pps.projeto.modumender.crud.CrudScanner;
import br.edu.ifpb.pps.projeto.modumender.crud.CrudHandler;
import br.edu.ifpb.pps.projeto.modumender.crud.CrudResourceDefinition;
import br.edu.ifpb.pps.projeto.modumender.crud.DefaultCrudResourceFactory;
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

    //IMPORT PARA O PADRÃO
    private static final List<CrudResourceDefinition> crudDefinitions = new ArrayList<>();

    @Override
    public void init() {
        System.out.println("FrameworkServlet init");

        ControllerScanner.scanControllers("br.edu.ifpb.pps.projeto.modumender.controller");
        routeDefinitions.addAll(ControllerScanner.getRouteDefinitions());

        // Criando a instância do scanner e passando a fábrica //caio
        //para se adequar ao padrão  Factory Method
        CrudScanner crudScanner = new CrudScanner(new DefaultCrudResourceFactory());
        crudScanner.scanCrudResources("br.edu.ifpb.pps.projeto.modumender.resources");

        // Adiciona os recursos CRUD escaneados
        crudDefinitions.addAll(crudScanner.getDefinitions());


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
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println("\n--- NOVA REQUISIÇÃO ---");
        String method = req.getMethod();
        String path = req.getPathInfo();
        if (path == null) path = "/";
        System.out.println("Método HTTP: " + method);
        System.out.println("Caminho: " + path);

        CookieAuthFilter authChecker = new CookieAuthFilter();
        boolean intercepted = authChecker.doAuthCheck(req, resp);
        if (intercepted) {
            System.out.println("⚠️ Requisição interceptada por auth");
            return;
        }

        HttpRequest request = new HttpRequest(req);
        HttpResponse response = new HttpResponse(resp);

        String renderedTemplate = TemplateRouteHandler.handleRequest(path, request);
        if (renderedTemplate != null) {
            System.out.println("✅ Template encontrado para rota: " + path);
            response.writeBody(renderedTemplate);
            return;
        } else {
            System.out.println("❌ Nenhum template encontrado para rota: " + path);
        }

        ControllerHandler handler = findMatchingHandler(method, path, request);
        if (handler == null) {
            System.out.println("❌ Nenhum handler encontrado para rota: " + method + " " + path);
            resp.setStatus(404);
            resp.getWriter().write("Rota não encontrada: " + path);
            return;
        }

        System.out.println("Invocando handler: "
                + handler.getControllerClass().getSimpleName()
                + "." + handler.method.getName());

        try {
            Object result = handler.invoke(request, response);
            System.out.println("✅ Resultado do handler: " + result);

            // se o controlador não retornou nada (null),
// MAS também não definiu status, colocamos 204
            if (result == null) {
                int currentStatus = resp.getStatus();
                System.out.println("⚠️ Resultado é null, status atual=" + currentStatus);
                // Se ainda estiver 200, trocamos para 204
                if (currentStatus == 200) {
                    resp.setStatus(204);
                }
                return;
            }



            if (result instanceof String && !isRestController(handler)) {
                response.writeBody((String) result);
            } else if (isRestController(handler)) {
                resp.setContentType("application/json");
                String json = JsonUtil.toJson(result);
                response.writeBody(json);
                System.out.println("✅ JSON enviado: " + json);
            } else {
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

        System.out.println("Comparando: " + template + " com " + actual);

        return params;
    }
}
