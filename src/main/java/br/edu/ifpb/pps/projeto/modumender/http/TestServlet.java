package br.edu.ifpb.pps.projeto.modumender.http;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class TestServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpRequest request = new HttpRequest(req);
        HttpResponse response = new HttpResponse(resp);

        response.setStatus(200);
        response.setHeader("Custom-Header", "Test");

        String responseMessage = "Método: " + request.getMethod() + "\n"
                + "Caminho: " + request.getPath() + "\n"
                + "Parâmetros: " + request.getParameters() + "\n"
                + "Headers: " + request.getHeaders();

        response.sendText(responseMessage);
    }
}