package br.edu.ifpb.pps.projeto.modumender.http;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Wrapper p/ HttpServletRequest,
 * oferecendo getParameter e pathParams
 * (se placeholders).
 */
public class HttpRequest {
    private final HttpServletRequest rawRequest;
    private final Map<String, String> pathParams = new HashMap<>();
    private final Map<String, String> headers = new HashMap<>(); // Inicializa o mapa

    public HttpRequest(HttpServletRequest rawRequest) {
        this.rawRequest = rawRequest;

        // Preenche o mapa com os headers reais da requisição
        rawRequest.getHeaderNames().asIterator().forEachRemaining(
                headerName -> headers.put(headerName, rawRequest.getHeader(headerName))
        );
    }

    //pega um parâmetro da URL ou do corpo
    public String getParameter(String name) {
        return rawRequest.getParameter(name);
    }

    public void setPathParam(String key, String value) {
        pathParams.put(key, value);
    }

    //usado quando você define rotas com placeholders como /usuario/{id}
    public String getPathParam(String key) {
        return pathParams.get(key);
    }

    //retorna o objeto original (HttpServletRequest) se precisar de algo mais avançado.
    public HttpServletRequest getRawRequest() {
        return rawRequest;
    }

    //acessa um cabeçalho (ex: Accept, Authorization
    public String getHeader(String name) {
        return headers.get(name); // Agora não dá mais NullPointerException
    }
}
