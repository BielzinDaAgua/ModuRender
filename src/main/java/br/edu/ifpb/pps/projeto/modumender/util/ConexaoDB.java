package br.edu.ifpb.pps.projeto.modumender.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexaoDB {
    private static Connection conexao;
    private static String URL;
    private static String USER;
    private static String PASSWORD;

    static {
        try {
            // Carrega propriedades do arquivo application.properties
            Properties properties = new Properties();
            properties.load(new FileInputStream("src/main/resources/application.properties"));

            URL = properties.getProperty("db.url");
            USER = properties.getProperty("db.username");
            PASSWORD = properties.getProperty("db.password");
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar arquivo de configuração.", e);
        }
    }

    private ConexaoDB() {}

    public static Connection getInstance() throws SQLException {
        if (conexao == null || conexao.isClosed()) {
            conexao = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexão estabelecida com o banco de dados!");
        }
        return conexao;
    }
}
