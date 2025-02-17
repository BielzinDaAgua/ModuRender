package br.edu.ifpb.modurender.utils;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;

public class CompilerUtil {

    /**
     * Compila o arquivo .java gerado.
     */
    public static void compileClass(String filePath) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        int result = compiler.run(null, null, null, filePath);

        if (result == 0) {
            System.out.println("Compilação bem-sucedida!");
        } else {
            System.err.println("Erro na compilação.");
        }
    }
}
