package br.edu.ifpb.modurender.utils;

import jakarta.persistence.*;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FieldAccessor;

import java.io.File;
import java.util.List;

public class DynamicEntityGenerator {

    public static Class<?> generateEntity(String entityName, List<String> attributes) throws Exception {
        ByteBuddy byteBuddy = new ByteBuddy();

        // Criando a classe dinamicamente com a anotação @Entity
        DynamicType.Builder<Object> builder = byteBuddy.subclass(Object.class)
                .name("br.edu.ifpb.modurender.generated." + entityName)
                .annotateType(AnnotationDescription.Builder.ofType(Entity.class).build());

        // Adicionando o atributo "id" como chave primária
        builder = builder.defineField("id", Long.class, Visibility.PRIVATE)
                .annotateField(AnnotationDescription.Builder.ofType(Id.class).build())
                .annotateField(AnnotationDescription.Builder.ofType(GeneratedValue.class)
                        .define("strategy", GenerationType.IDENTITY)
                        .build())
                .defineMethod("getId", Long.class, Visibility.PUBLIC)
                .intercept(FieldAccessor.ofField("id"))
                .defineMethod("setId", void.class, Visibility.PUBLIC)
                .withParameters(Long.class)
                .intercept(FieldAccessor.ofField("id"));

        // Adicionando os demais atributos
        for (String attribute : attributes) {
            String[] parts = attribute.split(" : ");
            String attributeName = parts[0];
            String attributeType = parts[1];

            builder = builder.defineField(attributeName, getClassForType(attributeType), Visibility.PRIVATE)
                    .annotateField(AnnotationDescription.Builder.ofType(Column.class).build())
                    .defineMethod("get" + capitalize(attributeName), getClassForType(attributeType), Visibility.PUBLIC)
                    .intercept(FieldAccessor.ofField(attributeName))
                    .defineMethod("set" + capitalize(attributeName), void.class, Visibility.PUBLIC)
                    .withParameters(getClassForType(attributeType))
                    .intercept(FieldAccessor.ofField(attributeName));
        }

        // Gerar a classe final
        DynamicType.Unloaded<?> dynamicClass = builder.make();

        // **Salvar como arquivo físico**
        String outputDirectory = "generated-classes"; // Diretório onde será salvo
        File outputDir = new File(outputDirectory);
        if (!outputDir.exists()) {
            outputDir.mkdirs(); // Cria o diretório, se necessário
        }

        // Salvar a classe como arquivo .class
        dynamicClass.saveIn(outputDir);

        // Carregar a classe gerada no ClassLoader
        return dynamicClass
                .load(DynamicEntityGenerator.class.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                .getLoaded();
    }

    private static Class<?> getClassForType(String type) throws ClassNotFoundException {
        return switch (type) {
            case "String" -> String.class;
            case "int" -> int.class;
            case "double" -> double.class;
            case "boolean" -> boolean.class;
            case "Date" -> java.util.Date.class;
            default -> throw new ClassNotFoundException("Tipo não suportado: " + type);
        };
    }

    private static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
