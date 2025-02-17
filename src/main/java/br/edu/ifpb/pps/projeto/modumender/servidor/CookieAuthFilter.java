package br.edu.ifpb.pps.projeto.modumender.servidor;

import br.edu.ifpb.pps.projeto.modumender.auth.AuthTokenUtil;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Exemplo de filtro que exige cookie authToken
 * para rotas exceto algumas livres.
 */
public class CookieAuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("CookieAuthFilter init");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp= (HttpServletResponse) response;

        String path = req.getRequestURI();
        // se rota for /login, /test, /hello => sem exigir token
        if (path.startsWith("/login")
                || path.startsWith("/test")
                || path.startsWith("/hello")) {
            chain.doFilter(request, response);
            return;
        }

        String token = getAuthToken(req.getCookies());
        if (token == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("Falta cookie authToken");
            return;
        }
        String user = AuthTokenUtil.validateToken(token);
        if (user == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("Token inválido ou expirado");
            return;
        }
        // ok
        req.setAttribute("authUser", user);
        chain.doFilter(request, response);
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

    @Override
    public void destroy() {
        System.out.println("CookieAuthFilter destroy");
    }
}
