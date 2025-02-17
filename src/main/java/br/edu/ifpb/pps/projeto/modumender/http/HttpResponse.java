package br.edu.ifpb.pps.projeto.modumender.http;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Wrapper p/ HttpServletResponse,
 * exibir mensagem final, set status etc.
 */
public class HttpResponse {
    private final HttpServletResponse rawResponse;

    public HttpResponse(HttpServletResponse rawResponse) {
        this.rawResponse = rawResponse;
    }

    public void setStatus(int status) {
        rawResponse.setStatus(status);
    }

    public void writeBody(String text) throws IOException {
        rawResponse.getWriter().write(text);
    }

    public HttpServletResponse getRawResponse() {
        return rawResponse;
    }
}
