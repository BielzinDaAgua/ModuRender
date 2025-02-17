package br.edu.ifpb.pps.projeto.modumender;

public class MainApp {
    public static void main(String[] args) {
        // Apenas gera as tabelas do BD (via SchemaGenerator)
        ModuRender.inicializar();
        System.out.println("Criação de tabelas e relacionamentos concluída!");
    }
}
