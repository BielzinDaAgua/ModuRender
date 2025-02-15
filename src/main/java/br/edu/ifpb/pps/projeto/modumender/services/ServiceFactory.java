package br.edu.ifpb.pps.projeto.modumender.services;

import br.edu.ifpb.pps.projeto.modumender.dao.DAOFactory;
import br.edu.ifpb.pps.projeto.modumender.dao.GenericDAO;

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
            // Em vez de "new GenericDAO<>(clazz)", use a DAOFactory:
            GenericDAO<T> dao = DAOFactory.createDAO(clazz);
            GenericService<T> service = new GenericService<>(dao);
            services.put(clazz, service);
        }
        return (GenericService<T>) services.get(clazz);
    }
}
