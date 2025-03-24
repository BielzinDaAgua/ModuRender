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

    //define o código da resposta HTTP.
    public void setStatus(int status) {
        rawResponse.setStatus(status);
    }

    //escreve o corpo da resposta (HTML, JSON, texto puro…).
    public void writeBody(String text) throws IOException {
        rawResponse.getWriter().write(text);
    }

    // define o tipo do conteúdo da resposta.
    public void setContentType(String type) {
        rawResponse.setContentType(type); // Método adicionado
    }

    //retorna o HttpServletResponse original, se necessário
    public HttpServletResponse getRawResponse() {
        return rawResponse;
    }
}
