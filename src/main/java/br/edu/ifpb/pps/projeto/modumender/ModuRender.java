package br.edu.ifpb.pps.projeto.modumender;

/**
 * Classe principal do framework.
 * Ao carregar, dispara a geração do schema (tabelas, FKs).
 */
public class ModuRender {

    static {
        SchemaGenerator.generateAllSchemas();
    }

    public static void inicializar() {
        System.out.println("🔥 ModuRender inicializado.");
    }
}
