package br.edu.ifpb.pps.projeto.modumender.servidor;

import br.edu.ifpb.pps.projeto.modumender.controller.ControllerHandler;
import br.edu.ifpb.pps.projeto.modumender.controller.ControllerScanner;
import br.edu.ifpb.pps.projeto.modumender.crud.CrudScanner;
import br.edu.ifpb.pps.projeto.modumender.http.HttpRequest;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlet.FilterHolder;
import jakarta.servlet.DispatcherType;

import java.util.EnumSet;

public class JettyServer {

    private Server server;
    private final int port = 8080;

    public void start() {
        try {
            // 1) Cria o servidor Jetty na porta configurada
            server = new Server(port);

            // 2) Cria um "ServletContextHandler" que servirá como "webapp"
            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
            context.setContextPath("/");

            // 3) Registrar servlets
            // (a) TestServlet
            ServletHolder testServletHolder = new ServletHolder("testServlet", new TestServlet());
            context.addServlet(testServletHolder, "/test");

            // (b) FrameworkServlet
            ServletHolder fwServletHolder = new ServletHolder("frameworkServlet", new FrameworkServlet());
            context.addServlet(fwServletHolder, "/*");

            // 4) Registrar filter CookieAuthFilter
            FilterHolder authFilterHolder = new FilterHolder(new CookieAuthFilter());
            context.addFilter(authFilterHolder, "/*", EnumSet.of(DispatcherType.REQUEST));

            // 5) Associar o context ao servidor
            server.setHandler(context);

            // 6) Iniciar o servidor
            server.start();
            System.out.println("🚀 JettyServer iniciado na porta " + port);
            System.out.println("Acesse http://localhost:" + port + "/test para teste simples");

            // 7) Escanear controladores MANUAIS (ex.: HomeController)
            ControllerScanner.scanControllers("br.edu.ifpb.pps.projeto.modumender.controller");

            // 8) Escanear CRUD Resources (ex.: UsuarioResource)
            CrudScanner.scanCrudResources("br.edu.ifpb.pps.projeto.modumender.resources");

            // 9) Testar as rotas após a inicialização
            testRoutes();

            // 10) Ficar aguardando
            server.join();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void stop() {
        if (server != null && server.isRunning()) {
            try {
                server.stop();
                System.out.println("🛑 JettyServer parado.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    private static void testRoutes() {
        FrameworkServlet servlet = new FrameworkServlet();
        System.out.println("\n🔬 Teste de correspondência de rotas:");
        testRoute(servlet, "GET", "/login");
        testRoute(servlet, "GET", "/hello");
    }

    private static void testRoute(FrameworkServlet servlet, String method, String path) {
        System.out.println("\n📢 Testando rota " + method + " " + path);

        ControllerHandler handler = servlet.testFindHandler(method, path);
        if (handler != null) {
            System.out.println("✅ Handler encontrado!");
        } else {
            System.out.println("❌ Nenhum handler encontrado!");
        }
    }
    public static void main(String[] args) {
        JettyServer js = new JettyServer();
        js.start();

        // Teste manual
        testRoutes();
    }

}
