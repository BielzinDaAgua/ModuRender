package br.edu.ifpb.pps.projeto.modumender.http;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class HttpResponse {
    private final HttpServletResponse response;

    public HttpResponse(HttpServletResponse response) {
        this.response = response;
    }

    public void setStatus(int statusCode) {
        response.setStatus(statusCode);
    }

    public void setHeader(String name, String value) {
        response.setHeader(name, value);
    }

    public void sendText(String text) throws IOException {
        response.setContentType("text/plain");
        PrintWriter writer = response.getWriter();
        writer.write(text);
        writer.flush();
    }

    public void sendJson(String json) throws IOException {
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.write(json);
        writer.flush();
    }
}