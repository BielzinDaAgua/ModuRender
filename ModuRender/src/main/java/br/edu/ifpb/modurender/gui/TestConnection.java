package br.edu.ifpb.modurender.gui;

import java.sql.Connection;
import java.sql.DriverManager;

public class TestConnection {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/modurender"; // Substitua pelo seu
        String username = "postgres"; // Substitua pelo seu
        String password = "12345"; // Substitua pelo seu

        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            if (conn != null) {
                System.out.println("Conexão com o banco de dados foi bem-sucedida!");
            } else {
                System.out.println("Falha na conexão com o banco.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

