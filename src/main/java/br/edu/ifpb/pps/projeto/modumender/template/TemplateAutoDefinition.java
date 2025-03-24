package br.edu.ifpb.pps.projeto.modumender.template;

/**
 * Representa metadados da rota associada a um template específico.
 */

public class TemplateAutoDefinition {
    private final String path;
    private final String templateName;
    private final Class<?> sourceClass;

    public TemplateAutoDefinition(String path, String templateName, Class<?> sourceClass) {
        this.path = path;
        this.templateName = templateName;
        this.sourceClass = sourceClass;
    }

    //caminho da rota HTTP.
    public String getPath() {
        return path;
    }

    //nome do arquivo HTML a ser renderizado.
    public String getTemplateName() {
        return templateName;
    }

    //classe associada que pode gerar um modelo (buildModel()).
    public Class<?> getSourceClass() {
        return sourceClass;
    }
}
