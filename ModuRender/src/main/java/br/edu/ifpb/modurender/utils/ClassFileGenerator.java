package br.edu.ifpb.modurender.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ClassFileGenerator {

    /**
     * Gera um arquivo .java baseado no nome da classe, no pacote e nos campos fornecidos.
     */
    public static void generateEntityClass(String className, String packageName, String fields) throws IOException {
        // Diretório do pacote
        String directory = "src/main/java/" + packageName.replace(".", "/");
        new File(directory).mkdirs();

        // Caminho do arquivo
        String filePath = directory + "/" + className + ".java";

        // Escrever o código da classe
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("package " + packageName + ";\n\n");
            writer.write("import jakarta.persistence.*;\n\n");
            writer.write("@Entity\n");
            writer.write("public class " + className + " {\n\n");
            writer.write("    @Id\n");
            writer.write("    @GeneratedValue(strategy = GenerationType.IDENTITY)\n");
            writer.write("    private Long id;\n\n");
            writer.write(fields);
            writer.write("\n    public Long getId() {\n");
            writer.write("        return id;\n");
            writer.write("    }\n");
            writer.write("    public void setId(Long id) {\n");
            writer.write("        this.id = id;\n");
            writer.write("    }\n");
            writer.write("}");
        }
    }

    public static String generateFields(String[] attributes, String[] types) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < attributes.length; i++) {
            String attribute = attributes[i];
            String type = types[i];

            builder.append("    private ").append(type).append(" ").append(attribute).append(";\n\n");

            // Getter
            builder.append("    public ").append(type).append(" get").append(capitalize(attribute)).append("() {\n");
            builder.append("        return ").append(attribute).append(";\n");
            builder.append("    }\n");

            // Setter
            builder.append("    public void set").append(capitalize(attribute)).append("(").append(type)
                    .append(" ").append(attribute).append(") {\n");
            builder.append("        this.").append(attribute).append(" = ").append(attribute).append(";\n");
            builder.append("    }\n\n");
        }
        return builder.toString();
    }

    private static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
