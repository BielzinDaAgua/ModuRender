package br.edu.ifpb.pps.projeto.modumender;


public class ModuRender {
    static {
        // Inicializar o esquema do banco de dados automaticamente
        SchemaGenerator.generateAllSchemas();
    }

    public static void inicializar() {
        System.out.println("ModuRender inicializado.");
    }
}