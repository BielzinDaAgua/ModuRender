package br.edu.ifpb.pps.projeto.modumender.util;

import br.edu.ifpb.pps.projeto.modumender.annotations.Email;
import br.edu.ifpb.pps.projeto.modumender.annotations.NotEmpty;
import br.edu.ifpb.pps.projeto.modumender.annotations.PasswordComplex;

import java.lang.reflect.Field;

/**
 * Faz validações customizadas com base em anotações:
 * (@NotEmpty, @Email, @PasswordComplex, etc.).
 */
public class ValidationUtil {

    public static void validate(Object obj) {
        if (obj == null) {
            throw new ValidationException("Objeto nulo não é válido!");
        }

        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);

                // 1) @NotEmpty (exige string não vazia)
                if (field.isAnnotationPresent(NotEmpty.class)) {
                    if (value == null) {
                        throw new ValidationException(erro(field, "não pode ser null"));
                    }
                    if (value instanceof String s && s.trim().isEmpty()) {
                        throw new ValidationException(erro(field, "não pode ser vazio"));
                    }
                }

                // 2) @Email
                if (field.isAnnotationPresent(Email.class)) {
                    if (value == null) {
                        throw new ValidationException(erro(field, "é null mas deveria ser um e-mail"));
                    }
                    if (value instanceof String s) {
                        // Validação bem simples: verifica se contém "@"
                        if (!s.contains("@")) {
                            throw new ValidationException(erro(field, "não é um e-mail válido (falta @)"));
                        }
                    } else {
                        throw new ValidationException(erro(field, "não é String para e-mail"));
                    }
                }

                // 3) @PasswordComplex
                PasswordComplex passAnn = field.getAnnotation(PasswordComplex.class);
                if (passAnn != null) {
                    if (value == null) {
                        throw new ValidationException(erro(field, "não pode ser null (senha obrigatória)"));
                    }
                    if (value instanceof String password) {
                        int len = password.length();
                        if (len < passAnn.min() || len > passAnn.max()) {
                            throw new ValidationException(erro(field,
                                    "senha deve ter entre " + passAnn.min() + " e " + passAnn.max() + " caracteres"));
                        }
                        if (passAnn.requireLetters()) {
                            // Verifica se tem ao menos uma letra
                            if (!password.matches(".*[a-zA-Z].*")) {
                                throw new ValidationException(erro(field,
                                        "senha deve conter ao menos uma letra"));
                            }
                        }
                        if (passAnn.requireDigits()) {
                            // Verifica se tem ao menos um dígito
                            if (!password.matches(".*\\d.*")) {
                                throw new ValidationException(erro(field,
                                        "senha deve conter ao menos um dígito"));
                            }
                        }
                    } else {
                        throw new ValidationException(erro(field, "não é String para senha"));
                    }
                }

                // Caso tenha outras anotações, repetimos a lógica

            } catch (IllegalAccessException e) {
                throw new ValidationException("Erro ao ler campo " + field.getName()
                        + ": " + e.getMessage());
            }
        }
    }

    /**
     * Helper para criar mensagem de erro com nome do campo.
     */
    private static String erro(Field f, String msg) {
        return "Campo '" + f.getName() + "': " + msg;
    }
}
