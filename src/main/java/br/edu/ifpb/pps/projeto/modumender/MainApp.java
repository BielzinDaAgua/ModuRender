package br.edu.ifpb.pps.projeto.modumender;

public class MainApp {
    public static void main(String[] args) {
        // Inicializa o framework (gera o schema/tabelas)
        ModuRender.inicializar();

        System.out.println("Criação de tabelas e relacionamentos concluída!");
    }
}
