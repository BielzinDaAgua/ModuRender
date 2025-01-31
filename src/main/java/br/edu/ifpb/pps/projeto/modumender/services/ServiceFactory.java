package br.edu.ifpb.pps.projeto.modumender.services;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory para obter instâncias de GenericService<T>
 * sem repetir "new GenericService<>(Classe.class)" toda hora.
 */
public class ServiceFactory {
    private static final Map<Class<?>, Object> services = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static <T> GenericService<T> getService(Class<T> clazz) {
        if (!services.containsKey(clazz)) {
            services.put(clazz, new GenericService<>(clazz));
        }
        return (GenericService<T>) services.get(clazz);
    }
}
