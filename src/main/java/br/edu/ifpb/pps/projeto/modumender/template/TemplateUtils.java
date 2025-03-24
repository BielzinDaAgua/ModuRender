package br.edu.ifpb.pps.projeto.modumender.template;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Method;
import java.util.Map;

public class TemplateUtils {
    /**
     * Tenta invocar public static Map<String,Object> buildModel() na classe
     * que está em TemplateAutoDefinition. Se não tiver, retorna Map.of().
     */
    public static Map<String,Object> invokeBuildModel(TemplateAutoDefinition def) {
        try {
            Class<?> cls = def.getSourceClass();
            Method m = cls.getMethod("buildModel");
            if (m.getReturnType().equals(Map.class)) {
                @SuppressWarnings("unchecked")
                Map<String,Object> res = (Map<String,Object>) m.invoke(null);
                return res != null ? res : Map.of();
            }
        } catch (NoSuchMethodException e) {
            // Não tem buildModel()
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("error", "Falha ao invocar buildModel: " + e.getMessage());
        }
        return Map.of();
    }
}