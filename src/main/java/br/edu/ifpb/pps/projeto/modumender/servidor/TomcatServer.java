package br.edu.ifpb.pps.projeto.modumender.servidor;

import jakarta.servlet.Servlet;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.StandardRoot;

import java.io.File;

public class TomcatServer {
    private Tomcat tomcat;
    private int port = 8080; // Porta do servidor

    public void start() {
        try {
            tomcat = new Tomcat();
            tomcat.setPort(port);

            // Criando diretório temporário para o Tomcat
            String tempDir = System.getProperty("java.io.tmpdir");
            tomcat.setBaseDir(tempDir);

            // Criando contexto do Tomcat
            StandardContext ctx = (StandardContext) tomcat.addWebapp("/", new File(tempDir).getAbsolutePath());
            System.out.println("📂 Contexto criado em: " + new File(tempDir).getAbsolutePath());

            // Evita erro de JSP se não precisar de JSP
            ctx.setConfigured(false);

            // Configurando recursos da aplicação
            WebResourceRoot resources = new StandardRoot(ctx);
            ctx.setResources(resources);

            // Adicionando um Servlet de teste
            Tomcat.addServlet(ctx, "testServlet", (Servlet) new TestServlet());
            ctx.addServletMappingDecoded("/test", "testServlet");

            // Iniciando o servidor
            tomcat.start();
            System.out.println("🚀 Servidor iniciado na porta " + port);
            System.out.println("✅ Acesse http://localhost:" + port + "/test para verificar!");
            tomcat.getServer().await(); // Mantém o servidor rodando
        } catch (LifecycleException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            if (tomcat != null) {
                tomcat.stop();
                tomcat.destroy();
                System.out.println("🛑 Servidor parado.");
            }
        } catch (LifecycleException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        TomcatServer server = new TomcatServer();
        server.start();
    }
}
