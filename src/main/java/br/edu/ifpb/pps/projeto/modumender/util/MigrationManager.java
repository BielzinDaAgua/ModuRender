package br.edu.ifpb.pps.projeto.modumender.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class MigrationManager {

    public static void criarTabelas() {
        try (Connection conn = ConexaoDB.getInstance();
             Statement stmt = conn.createStatement()) {

            System.out.println("Iniciando a criação das tabelas...");

            // Desativar restrições de chaves estrangeiras temporariamente (caso necessário)
            stmt.execute("SET session_replication_role = 'replica';");

            // Criar a tabela de usuários
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS usuarios (" +
                            "    id SERIAL PRIMARY KEY, " +
                            "    nome VARCHAR(100) NOT NULL, " +
                            "    email VARCHAR(100) UNIQUE NOT NULL, " +
                            "    senha VARCHAR(255) NOT NULL, " +
                            "    tipo_usuario VARCHAR(20) NOT NULL" +
                            ");"
            );

            // Criar a tabela de cursos
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS cursos (" +
                            "    id SERIAL PRIMARY KEY, " +
                            "    titulo VARCHAR(100) NOT NULL, " +
                            "    descricao TEXT NOT NULL, " +
                            "    preco NUMERIC(10, 2) NOT NULL, " +
                            "    data_criacao DATE NOT NULL, " +
                            "    instrutor_id INT NOT NULL, " +
                            "    CONSTRAINT fk_instrutor FOREIGN KEY (instrutor_id) REFERENCES usuarios(id) ON DELETE CASCADE" +
                            ");"
            );

            // Criar a tabela de categorias
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS categorias (" +
                            "    id SERIAL PRIMARY KEY, " +
                            "    nome VARCHAR(100) NOT NULL, " +
                            "    descricao TEXT" +
                            ");"
            );

            // Criar a tabela de matrículas
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS matriculas (" +
                            "    id SERIAL PRIMARY KEY, " +
                            "    usuario_id INT NOT NULL, " +
                            "    curso_id INT NOT NULL, " +
                            "    data_matricula DATE NOT NULL, " +
                            "    status VARCHAR(20) NOT NULL, " +
                            "    CONSTRAINT fk_usuario_matricula FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE, " +
                            "    CONSTRAINT fk_curso_matricula FOREIGN KEY (curso_id) REFERENCES cursos(id) ON DELETE CASCADE" +
                            ");"
            );

            // Criar a tabela de avaliações
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS avaliacoes (" +
                            "    id SERIAL PRIMARY KEY, " +
                            "    usuario_id INT NOT NULL, " +
                            "    curso_id INT NOT NULL, " +
                            "    nota INT NOT NULL CHECK (nota BETWEEN 1 AND 5), " +
                            "    comentario TEXT NOT NULL, " +
                            "    data_avaliacao DATE NOT NULL, " +
                            "    CONSTRAINT fk_usuario_avaliacao FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE, " +
                            "    CONSTRAINT fk_curso_avaliacao FOREIGN KEY (curso_id) REFERENCES cursos(id) ON DELETE CASCADE" +
                            ");"
            );

            // Criar a tabela de certificados
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS certificados (" +
                            "    id SERIAL PRIMARY KEY, " +
                            "    usuario_id INT NOT NULL, " +
                            "    curso_id INT NOT NULL, " +
                            "    data_emissao DATE NOT NULL, " +
                            "    CONSTRAINT fk_usuario_certificado FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE, " +
                            "    CONSTRAINT fk_curso_certificado FOREIGN KEY (curso_id) REFERENCES cursos(id) ON DELETE CASCADE" +
                            ");"
            );

            // Reativar restrições de chaves estrangeiras
            stmt.execute("SET session_replication_role = 'origin';");

            System.out.println("Todas as tabelas foram criadas com sucesso!");

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erro ao criar as tabelas no banco de dados.");
        }
    }
}
