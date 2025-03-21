package br.edu.ifpb.pps.projeto.modumender.template;

public class TemplateAutoDefinition {
    private final String path;
    private final String templateName;
    private final Class<?> sourceClass;

    public TemplateAutoDefinition(String path, String templateName, Class<?> sourceClass) {
        this.path = path;
        this.templateName = templateName;
        this.sourceClass = sourceClass;
    }

    public String getPath() {
        return path;
    }

    public String getTemplateName() {
        return templateName;
    }

    public Class<?> getSourceClass() {
        return sourceClass;
    }
}
