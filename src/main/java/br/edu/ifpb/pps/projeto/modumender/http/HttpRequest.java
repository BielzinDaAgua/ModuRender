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

    public HttpRequest(HttpServletRequest rawRequest) {
        this.rawRequest = rawRequest;
    }

    public String getParameter(String name) {
        return rawRequest.getParameter(name);
    }

    public void setPathParam(String key, String value) {
        pathParams.put(key, value);
    }
    public String getPathParam(String key) {
        return pathParams.get(key);
    }

    public HttpServletRequest getRawRequest() {
        return rawRequest;
    }
}
