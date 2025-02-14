package test;

import br.edu.ifpb.pps.projeto.modumender.ModuRender;
import br.edu.ifpb.pps.projeto.modumender.dao.GenericDAO;
import br.edu.ifpb.pps.projeto.modumender.model.Usuario;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Exemplo de teste com JUnit 5 para validar a classe Usuario.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UsuarioDAOTest {

    private static GenericDAO<Usuario> usuarioDAO;

    @BeforeAll
    public static void setup() {
        // Inicia o framework (gera tabelas)
        //ModuRender.inicializar();
        // Cria o DAO generico para Usuario
        usuarioDAO = new GenericDAO<>(Usuario.class);
    }

    @Test
    @Order(1)
    public void shouldInsertValidUser() throws SQLException {
        Usuario u = new Usuario();
        u.setId(1);
        u.setNome("Joao Silva");      // >= 3 chars
        u.setEmail("joao@ifpb.edu");  // >= 5 chars
        u.setSenha("123456");         // >= 6 chars
        u.setTipoUsuario("ALUNO");    // 5 chars, ok

        assertDoesNotThrow(() -> {
            usuarioDAO.save(u);
        }, "Não deve lançar exceção para usuário válido");
    }

    @Test
    @Order(2)
    public void shouldFailWhenNameTooShort() {
        Usuario u = new Usuario();
        u.setId(2);
        u.setNome("Jo");             // Somente 2 chars, viola @Length(min=3)
        u.setEmail("jose@ifpb.edu");
        u.setSenha("123456");
        u.setTipoUsuario("ALUNO");

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            usuarioDAO.save(u);  // deve falhar
        });

        assertTrue(ex.getMessage().contains("nome"));
    }

    @Test
    @Order(3)
    public void shouldFailWhenPasswordTooLong() {
        Usuario u = new Usuario();
        u.setId(3);
        u.setNome("Jose Maria");
        u.setEmail("jm@ifpb.edu");
        // 21 chars (maior que max=20)
        u.setSenha("123456789012345678901");
        u.setTipoUsuario("ALUNO");

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            usuarioDAO.save(u);
        });

        assertTrue(ex.getMessage().contains("senha"));
    }

    @Test
    @Order(4)
    public void shouldListAllUsers() throws SQLException {
        List<Usuario> all = usuarioDAO.findAll();
        // Já inserimos 1 usuário válido, e 2 tentativas que falharam.
        // Então deve haver 1 registro persistido.
        assertEquals(1, all.size(), "Deve ter 1 usuário na lista");
    }

    @Test
    @Order(5)
    public void shouldFindById() throws SQLException {
        Usuario u = usuarioDAO.findById(1);
        assertNotNull(u);
        assertEquals("Joao Silva", u.getNome());
    }
}