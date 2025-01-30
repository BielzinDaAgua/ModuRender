package test;

import br.edu.ifpb.pps.projeto.modumender.services.GenericService;
import br.edu.ifpb.pps.projeto.modumender.util.ConexaoDB;
import br.edu.ifpb.pps.projeto.modumender.services.ServiceFactory;
import br.edu.ifpb.pps.projeto.modumender.models.Usuario;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class AppTest {
    public static void main(String[] args) {
        try {
            // 1️⃣ Testar Conexão com o Banco de Dados
            Connection connection = ConexaoDB.getInstance();
            if (connection != null) {
                System.out.println("✅ Conexão com o banco de dados estabelecida com sucesso!");
            }

            // 2️⃣ Criar Serviço para a Entidade User
            GenericService<Usuario> userService = ServiceFactory.getService(Usuario.class);

            // 3️⃣ Criar e Inserir um Usuário no Banco de Dados
            Usuario user = new Usuario();
            user.setNome("Caio");
            user.setId(1);
            user.setEmail("caio@ifpb.edu.br");


            userService.save(user);
            System.out.println("✅ Usuário salvo com sucesso!");

            // 4️⃣ Buscar Usuário pelo ID
            Usuario userFromDb = userService.findById(1);
            if (userFromDb != null) {
                System.out.println("✅ Usuário encontrado: " + userFromDb.getNome());
            }

            // 5️⃣ Listar Todos os Usuários
            List<Usuario> users = userService.findAll();
            System.out.println("✅ Lista de Usuários:");
            for (Usuario u : users) {
                System.out.println(" - " + u.getNome() + " | " + u.getEmail());
            }

            // 6️⃣ Testar Atualização de Usuário
            userFromDb.setNome("Caio B. Quirino");
            userService.save(userFromDb);
            System.out.println("✅ Usuário atualizado!");

            // 7️⃣ Testar Exclusão de Usuário
            userService.deleteById(1);
            System.out.println("✅ Usuário deletado com sucesso!");
//
        } catch (SQLException e) {
            System.err.println("❌ Erro: " + e.getMessage());
        }
    }
}