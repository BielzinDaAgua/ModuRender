package br.edu.ifpb.pps.projeto.modumender.controller;

import br.edu.ifpb.pps.projeto.modumender.http.HttpRequest;
import br.edu.ifpb.pps.projeto.modumender.http.HttpResponse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 Encapsular a invocação de métodos do controller usando reflexão.

 É usada quando o seu framework define controllers com métodos específicos para cada rota.

 Ao receber uma requisição, esta classe chama o método exato do controller correspondente à rota.
 */
public class MethodRouteCommand implements RouteCommand {

    private final Object controllerInstance;
    private final Method controllerMethod;

    public MethodRouteCommand(Object controllerInstance, Method controllerMethod) {
        this.controllerInstance = controllerInstance;
        this.controllerMethod = controllerMethod;
    }

    // GETTER para permitir isRestControllerCommand(...) no FrameworkServlet
    public Object getControllerInstance() {
        return this.controllerInstance;
    }

    //O método resolveArguments identifica quais argumentos o método do controller precisa (ex: HttpRequest ou HttpResponse) e injeta automaticamente.
    @Override
    public Object execute(HttpRequest req, HttpResponse resp) throws Exception {
        try {
            Class<?>[] paramTypes = controllerMethod.getParameterTypes();
            Object[] args = resolveArguments(paramTypes, req, resp);
            // Invoca o método real do controller:
            return controllerMethod.invoke(controllerInstance, args);
        } catch (InvocationTargetException e) {
            throw (Exception) e.getTargetException();
        }
    }

    private Object[] resolveArguments(Class<?>[] paramTypes, HttpRequest req, HttpResponse resp) {
        Object[] result = new Object[paramTypes.length];
        for (int i = 0; i < paramTypes.length; i++) {
            if (paramTypes[i].equals(HttpRequest.class)) {
                result[i] = req;
            } else if (paramTypes[i].equals(HttpResponse.class)) {
                result[i] = resp;
            } else {
                // se tiver @PathParam, etc. parseie aqui
                result[i] = null;
            }
        }
        return result;
    }
}
