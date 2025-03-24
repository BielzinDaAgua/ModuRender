package br.edu.ifpb.pps.projeto.modumender.servidor;

import br.edu.ifpb.pps.projeto.modumender.auth.AuthTokenUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Classe para checar token em cookie authToken,
 * sem usar a interface jakarta.servlet.Filter.
 *
 * Em vez disso, chamamos manualmente do FrameworkServlet.
 */
public class CookieAuthFilter {

    /**
     * Retorna true se a requisição foi interceptada
     * (ex.: sem token ou token inválido),
     * significando que devemos encerrar a request.
     * Se retornar false, podemos prosseguir normalmente.
     */

    //doAuthCheck-Verifica se a requisição tem um cookie de autenticação válido
    //Se o token estiver ausente ou for inválido, a requisição é interrompida imediatamente ( return true).
    public boolean doAuthCheck(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getRequestURI();

        // Rotas livres
        if (path.startsWith("/login")
                || path.startsWith("/test")
                || path.startsWith("/hello")) {
            return false; // não intercepta
        }

        // Checa cookie
        String token = getAuthToken(req.getCookies());
        if (token == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("Falta cookie authToken");
            return true; // Interceptamos, não prosseguir
        }

        String user = AuthTokenUtil.validateToken(token);
        if (user == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("Token inválido ou expirado");
            return true;
        }

        // Se válido, coloca o usuário no request
        //Ele adiciona informações à requisição para uso posterior  - padrão Intercepting Filter
        req.setAttribute("authUser", user);
        return false; // Não intercepta, pode prosseguir
    }

    private String getAuthToken(Cookie[] cookies) {
        if (cookies == null) return null;
        for (Cookie c : cookies) {
            if ("authToken".equals(c.getName())) {
                return c.getValue();
            }
        }
        return null;
    }
}
