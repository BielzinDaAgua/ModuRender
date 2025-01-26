package br.edu.ifpb.modurender.gui;

import br.edu.ifpb.modurender.core.EntityProcessor;
import br.edu.ifpb.modurender.utils.HibernateUtil;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class FrameworkGUI extends JFrame {
    private final EntityProcessor entityProcessor = new EntityProcessor(); // Instância global do processador

    public FrameworkGUI() {
        setTitle("Modurender - Framework");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        // Header
        JLabel header = new JLabel("Modurender Framework", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 24));
        header.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(header, BorderLayout.NORTH);

        // Main Panel (Centro)
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(2, 1, 10, 10));

        // Botão "Adicionar Entidade"
        JButton btnAddEntity = new JButton("Adicionar Nova Entidade");
        btnAddEntity.setFont(new Font("Arial", Font.BOLD, 16));
        btnAddEntity.addActionListener(e -> new AddEntityForm(entityProcessor));
        mainPanel.add(btnAddEntity);

        // Mensagem Informativa
        JLabel infoLabel = new JLabel("<html><center>As entidades serão adicionadas e salvas automaticamente no banco de dados.<br>"
                + "Use o botão acima para criar novas entidades.</center></html>", SwingConstants.CENTER);
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        mainPanel.add(infoLabel);

        add(mainPanel, BorderLayout.CENTER);

        // Rodapé (Bottom)
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton btnExit = new JButton("Sair");
        btnExit.setFont(new Font("Arial", Font.PLAIN, 16));
        btnExit.addActionListener(e -> System.exit(0));
        footerPanel.add(btnExit);
        add(footerPanel, BorderLayout.SOUTH);

        // Exibir janela
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        try {
            // Inicializar o Hibernate
            HibernateUtil.initializeSessionFactory();

            // Registrar uma entidade de exemplo
            EntityProcessor processor = new EntityProcessor();
            processor.addEntity("Test", List.of("nome : String", "idade : int"));

            System.out.println("Sistema inicializado com sucesso.");

            // Iniciar a interface gráfica
            SwingUtilities.invokeLater(FrameworkGUI::new);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao iniciar o sistema: " + e.getMessage());
        }
    }

}
