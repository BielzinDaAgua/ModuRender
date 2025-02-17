package br.edu.ifpb.modurender.gui;

import br.edu.ifpb.modurender.core.EntityProcessor;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AddEntityForm extends JFrame {
    private JTextField entityNameField;
    private final JPanel attributesPanel;
    private final EntityProcessor entityProcessor;
    private final List<JTextField> attributeNameFields = new ArrayList<>();
    private final List<JComboBox<String>> attributeTypeCombos = new ArrayList<>();

    public AddEntityForm(EntityProcessor entityProcessor) {
        this.entityProcessor = entityProcessor;

        setTitle("Adicionar Entidade");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 500);
        setLayout(new BorderLayout());

        // Painel superior para o nome da entidade
        JPanel entityPanel = new JPanel(new FlowLayout());
        JLabel entityNameLabel = new JLabel("Nome da Entidade:");
        entityNameField = new JTextField(20);
        entityPanel.add(entityNameLabel);
        entityPanel.add(entityNameField);
        add(entityPanel, BorderLayout.NORTH);

        // Painel dinâmico para os atributos
        attributesPanel = new JPanel();
        attributesPanel.setLayout(new BoxLayout(attributesPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(attributesPanel);
        scrollPane.setPreferredSize(new Dimension(580, 300));

        // Botão para adicionar novos atributos
        JButton addAttributeButton = new JButton("Adicionar Atributo");
        addAttributeButton.addActionListener(e -> addAttributeField());

        // Painel combinado de atributos e botão
        JPanel attributeContainerPanel = new JPanel();
        attributeContainerPanel.setLayout(new BorderLayout());
        attributeContainerPanel.add(scrollPane, BorderLayout.CENTER);
        attributeContainerPanel.add(addAttributeButton, BorderLayout.SOUTH);

        add(attributeContainerPanel, BorderLayout.CENTER);

        // Painel para salvar a entidade
        JPanel savePanel = new JPanel(new FlowLayout());
        JButton saveEntityButton = new JButton("Salvar Entidade");
        saveEntityButton.addActionListener(e -> saveEntity());
        savePanel.add(saveEntityButton);

        add(savePanel, BorderLayout.PAGE_END);

        // Exibir o formulário
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void addAttributeField() {
        JPanel attributeRow = new JPanel(new FlowLayout());

        // Campo para nome do atributo
        JTextField attributeNameField = new JTextField(15);
        attributeNameFields.add(attributeNameField);

        // ComboBox para tipo do atributo
        JComboBox<String> attributeTypeCombo = new JComboBox<>(new String[]{"String", "int", "double", "boolean", "Date"});
        attributeTypeCombos.add(attributeTypeCombo);

        attributeRow.add(new JLabel("Nome do Atributo:"));
        attributeRow.add(attributeNameField);
        attributeRow.add(new JLabel("Tipo:"));
        attributeRow.add(attributeTypeCombo);

        attributesPanel.add(attributeRow);
        attributesPanel.revalidate(); // Atualiza o painel dinâmico
        attributesPanel.repaint();
    }

    private void saveEntity() {
        String entityName = entityNameField.getText();
        if (entityName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, insira um nome para a entidade.");
            return;
        }

        List<String> attributes = new ArrayList<>();
        for (int i = 0; i < attributeNameFields.size(); i++) {
            String attributeName = attributeNameFields.get(i).getText();
            String attributeType = (String) attributeTypeCombos.get(i).getSelectedItem();

            if (attributeName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todos os atributos devem ter um nome.");
                return;
            }

            attributes.add(attributeName + " : " + attributeType);
        }

        if (attributes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Adicione pelo menos um atributo.");
            return;
        }

        try {
            entityProcessor.addEntity(entityName, attributes);
            JOptionPane.showMessageDialog(this, "Entidade " + entityName + " salva com sucesso!");
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar entidade: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
