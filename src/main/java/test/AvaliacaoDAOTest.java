package test;

import br.edu.ifpb.pps.projeto.modumender.ModuRender;
import br.edu.ifpb.pps.projeto.modumender.dao.GenericDAO;
import br.edu.ifpb.pps.projeto.modumender.model.Avaliacao;
import br.edu.ifpb.pps.projeto.modumender.model.Usuario;
import br.edu.ifpb.pps.projeto.modumender.model.Curso;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Date;
import java.sql.SQLException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AvaliacaoDAOTest {

    private static GenericDAO<Avaliacao> avaliacaoDAO;
    private static GenericDAO<Usuario> usuarioDAO;
    private static GenericDAO<Curso> cursoDAO;

    @BeforeAll
    public static void setupAll() {
        //ModuRender.inicializar();
        avaliacaoDAO = new GenericDAO<>(Avaliacao.class);
        usuarioDAO = new GenericDAO<>(Usuario.class);
        cursoDAO = new GenericDAO<>(Curso.class);

        // Você poderia inserir um Usuario e Curso válidos antes de testar Avaliacao.
        // Exemplo:
        try {
            Usuario user = new Usuario(10, "Fulano", "fulano@ifpb.edu", "123456", "ALUNO");
            usuarioDAO.save(user);

            Curso c = new Curso(100, "POO", "Curso de POO", 200.0, new Date(System.currentTimeMillis()), 10);
            // instrutorId=10 é fictício
            cursoDAO.save(c);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void shouldSaveValidAvaliacao() {
        Avaliacao av = new Avaliacao();
        av.setId(1);
        // Para ManyToOne usuario e curso, apontamos para IDs já existentes
        // (ou passamos objetos já carregados).
        Usuario user = new Usuario();
        user.setId(10);   // se já existe no BD
        av.setUsuario(user);

        Curso c = new Curso();
        c.setId(100); // se já existe no BD
        av.setCurso(c);

        av.setNota(8);  // dentro de [0..10]
        av.setComentario("Curso muito bom!");
        av.setDataAvaliacao(new Date(System.currentTimeMillis()));

        assertDoesNotThrow(() -> {
            avaliacaoDAO.save(av);
        });
    }

    @Test
    @Order(2)
    public void shouldFailWhenNotaIsOutOfRange() {
        Avaliacao av = new Avaliacao();
        av.setId(2);

        Usuario user = new Usuario();
        user.setId(10);
        av.setUsuario(user);

        Curso c = new Curso();
        c.setId(100);
        av.setCurso(c);

        av.setNota(15); // viola @Max(10)
        av.setComentario("Nota acima do permitido");
        av.setDataAvaliacao(new Date(System.currentTimeMillis()));

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            avaliacaoDAO.save(av);
        });
        assertTrue(ex.getMessage().contains("nota"));
    }
}