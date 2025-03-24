package br.edu.ifpb.pps.projeto.modumender.servidor;

import br.edu.ifpb.pps.projeto.modumender.controller.*;

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
 * Servlet principal. Agora, usando Command e Template Method.
 */
public class FrameworkServlet extends HttpServlet {

    private static final List<RouteDefinition> routeDefinitions = new ArrayList<>();

    // IMPORT PARA O CRUD (Factory Method)
    private static final List<CrudResourceDefinition> crudDefinitions = new ArrayList<>();

    @Override
    public void init() {
        System.out.println("FrameworkServlet init");

        // 1) Escaneia controllers e obtém rotaDefinitions
        ControllerScanner.scanControllers("br.edu.ifpb.pps.projeto.modumender.controller");
        routeDefinitions.addAll(ControllerScanner.getRouteDefinitions());

        // 2) Configurar CRUD
        CrudScanner crudScanner = new CrudScanner(new DefaultCrudResourceFactory());
        crudScanner.scanCrudResources("br.edu.ifpb.pps.projeto.modumender.resources");
        crudDefinitions.addAll(crudScanner.getDefinitions());

        // Para cada definição de CRUD, cria rotas usando RouteCommandFunctional
        for (CrudResourceDefinition def : CrudScanner.getDefinitions()) {
            String base = def.getBasePath();
            var handler = new CrudHandler(def.getEntityClass());

            routeDefinitions.add(new RouteDefinition("GET", base,
                    new RouteCommandFunctional((req, resp) -> handler.listAll(req, resp)), true));

            routeDefinitions.add(new RouteDefinition("GET", base + "/{id}",
                    new RouteCommandFunctional((req, resp) -> handler.findById(req, resp)), true));

            routeDefinitions.add(new RouteDefinition("POST", base,
                    new RouteCommandFunctional((req, resp) -> handler.create(req, resp)), true));

            routeDefinitions.add(new RouteDefinition("PUT", base + "/{id}",
                    new RouteCommandFunctional((req, resp) -> handler.update(req, resp)), true));

            routeDefinitions.add(new RouteDefinition("DELETE", base + "/{id}",
                    new RouteCommandFunctional((req, resp) -> handler.delete(req, resp)), true));


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



        // Filtro de autenticação (exemplo)
        CookieAuthFilter authChecker = new CookieAuthFilter();
        boolean intercepted = authChecker.doAuthCheck(req, resp);
        if (intercepted) {
            System.out.println("⚠️ Requisição interceptada por auth");
            return;
        }

        // Adapta HttpServletRequest/Response para HttpRequest/HttpResponse
        HttpRequest request = new HttpRequest(req);
        HttpResponse response = new HttpResponse(resp);

        // Primeiro tenta renderizar algum Template (TemplateMethod)
        String renderedTemplate = TemplateRouteHandler.handleRequest(path, request);
        if (renderedTemplate != null) {
            System.out.println("✅ Template encontrado para rota: " + path);
            response.writeBody(renderedTemplate);
            return;
        } else {
            System.out.println("❌ Nenhum template encontrado para rota: " + path);
        }

        // Se não é template, procura um RouteCommand correspondente
        RouteCommand command = findMatchingCommand(method, path, request);
        if (command == null) {
            System.out.println("❌ Nenhum comando encontrado para rota: " + method + " " + path);
            resp.setStatus(404);
            resp.getWriter().write("Rota não encontrada: " + path);
            return;
        }

        try {
            // Executa o comando
            Object result = command.execute(request, response);
            System.out.println("✅ Resultado do comando: " + result);

            // Se o comando retornou null e não setou status, é 204
            if (result == null) {
                int currentStatus = resp.getStatus();
                System.out.println("⚠️ Resultado é null, status atual=" + currentStatus);
                if (currentStatus == 200) {
                    resp.setStatus(204);
                }
                return;
            }

            // Caso o command esteja em um @RestController, retornamos JSON
            if (isRestControllerCommand(command)) {
                resp.setContentType("application/json");
                String json = JsonUtil.toJson(result);
                response.writeBody(json);
                System.out.println("✅ JSON enviado: " + json);
            } else {
                // Retorno de texto simples
                response.writeBody(result.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
            resp.getWriter().write("Erro interno: " + e.getMessage());
        }
    }

    /**
     * Verifica se o comando está vinculado a uma classe com @RestController.
     * Se você estiver usando 'MethodRouteCommand', por exemplo, pode checar a classe interna.
     */
    private boolean isRestControllerCommand(RouteCommand command) {
        for (RouteDefinition def : routeDefinitions) {
            if (def.getCommand().equals(command)) {
                return def.isRest();
            }
        }
        return false;
    }




    /**
     * Encontra o comando cuja rota bate com (method, path).
     */
    private RouteCommand findMatchingCommand(String method, String path, HttpRequest req) {
        for (RouteDefinition rd : routeDefinitions) {
            if (!rd.getHttpMethod().equalsIgnoreCase(method)) continue;

            var matchResult = matchTemplate(rd.getPathTemplate(), path);
            if (matchResult != null) {
                matchResult.forEach(req::setPathParam);
                return rd.getCommand(); // Retorna o RouteCommand
            }
        }
        return null;
    }

    /**
     * Faz o match do path (ex.: "/api/usuarios/{id}") com a URL concreta (ex.: "/api/usuarios/10").
     * Se combinar, retorna mapa de pathParams; se não, retorna null.
     */
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
