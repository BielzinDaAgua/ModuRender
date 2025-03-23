package br.edu.ifpb.pps.projeto.modumender.annotations;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordComplex {
    int min() default 8;
    int max() default 20;
    boolean requireLetters() default true;
    boolean requireDigits() default true;
    // poderia adicionar "boolean requireSpecial() default false;" etc.
}
