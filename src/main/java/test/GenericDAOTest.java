package test;

import br.edu.ifpb.pps.projeto.modumender.dao.GenericDAO;
import br.edu.ifpb.pps.projeto.modumender.models.Usuario;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GenericDAOTest {
    private static GenericDAO<Usuario> usuarioDAO;

    @BeforeAll
    static void setup() {
        usuarioDAO = new GenericDAO<>(Usuario.class);
    }

    @Test
    @Order(1)
    void testSalvarUsuario() throws SQLException {
        Usuario user = new Usuario();
        user.setId(1);
        user.setNome("Teste User");
        user.setEmail("teste@ifpb.edu.br");

        assertDoesNotThrow(() -> usuarioDAO.save(user));
    }

    @Test
    @Order(2)
    void testBuscarUsuarioPorId() throws SQLException {
        Usuario user = usuarioDAO.findById(1);
        assertNotNull(user);
        assertEquals("Teste User", user.getNome());
    }

    @Test
    @Order(3)
    void testListarTodosUsuarios() throws SQLException {
        List<Usuario> usuarios = usuarioDAO.findAll();
        assertFalse(usuarios.isEmpty());
    }

    @Test
    @Order(4)
    void testDeletarUsuario() throws SQLException {
        usuarioDAO.deleteById(1);
        Usuario user = usuarioDAO.findById(1);
        assertNull(user);
    }
}
