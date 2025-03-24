package br.edu.ifpb.pps.projeto.modumender.servidor;

import br.edu.ifpb.pps.projeto.modumender.controller.ControllerScanner;
import br.edu.ifpb.pps.projeto.modumender.crud.CrudScanner;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.net.URL;

public class JettyServer {

    private Server server;
    private final int port = 8080;

    public void start() {
        try {
            server = new Server(port);
            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
            context.setContextPath("/");

            // Primeiro o servlet estático:
            ServletHolder staticHolder = new ServletHolder("static", DefaultServlet.class);

            // 👇 Forma mais segura de garantir localização correta dos arquivos estáticos:
            String staticBase = JettyServer.class
                    .getClassLoader()
                    .getResource("static/")
                    .toExternalForm();

            staticHolder.setInitParameter("resourceBase", staticBase);
            staticHolder.setInitParameter("dirAllowed", "true");
            staticHolder.setInitParameter("pathInfoOnly", "true");
            context.addServlet(staticHolder, "/static/*");

            // Depois o seu servlet principal (framework):
            context.addServlet(new ServletHolder("frameworkServlet", new FrameworkServlet()), "/*");

            server.setHandler(context);
            server.start();

            System.out.println("JettyServer iniciado na porta " + port);


            server.join();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void stop() {
        if (server != null && server.isRunning()) {
            try {
                server.stop();
                System.out.println("JettyServer parado.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new JettyServer().start();
    }
}
