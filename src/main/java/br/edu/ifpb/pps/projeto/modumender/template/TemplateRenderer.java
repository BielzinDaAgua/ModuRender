package br.edu.ifpb.pps.projeto.modumender.template;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.util.Map;

public class TemplateRenderer {
    private static final TemplateEngine templateEngine;

    static {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/"); // Define a pasta onde os templates estarão
        resolver.setSuffix(".html"); // Define a extensão padrão dos arquivos
        resolver.setTemplateMode("HTML");
        resolver.setCharacterEncoding("UTF-8");

        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(resolver);
    }

    public static String render(String templateName, Map<String, Object> model) {
        Context context = new Context();
        model.forEach(context::setVariable);
        return templateEngine.process(templateName, context);
    }
}
