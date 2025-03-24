package br.edu.ifpb.pps.projeto.modumender.controller;

import br.edu.ifpb.pps.projeto.modumender.annotations.Controller;
import br.edu.ifpb.pps.projeto.modumender.annotations.Route;
import br.edu.ifpb.pps.projeto.modumender.http.HttpRequest;
import br.edu.ifpb.pps.projeto.modumender.http.HttpResponse;
import br.edu.ifpb.pps.projeto.modumender.auth.AuthTokenUtil;
import jakarta.servlet.http.Cookie;

@Controller
public class HomeController {

    @Route(path = "/hello", method = "GET")
    public String sayHello(HttpRequest req, HttpResponse resp) {

        return "Olá, essa rota é pública! Você pode usar /login para se autenticar.";
    }

    @Route(path = "/login", method = "GET")
    public String exibirFormLogin(HttpRequest req, HttpResponse resp) {
        resp.setContentType("text/html");  // <--- essencial!

        return "<html><body>"
                + "<form method='POST' action='/login'>"
                + "User: <input name='user'><br>"
                + "Pass: <input name='pass' type='password'><br>"
                + "<input type='submit' value='Login'>"
                + "</form>"
                + "</body></html>";
    }



    @Route(path = "/login", method = "POST")
    public String doLogin(HttpRequest req, HttpResponse resp) {
        String user = req.getParameter("user");
        String pass = req.getParameter("pass");
        if ("admin".equals(user) && "123".equals(pass)) {
            String token = AuthTokenUtil.generateToken(user);
            Cookie cookie = new Cookie("authToken", token);
            cookie.setPath("/");
            resp.getRawResponse().addCookie(cookie);
            return "Login bem-sucedido. Cookie authToken gerado!";
        } else {
            resp.setStatus(401);
            return "Credenciais inválidas!";
        }
    }

    // rota segura (exige token)
    @Route(path = "/admin/painel", method = "GET")
    public String adminPanel(HttpRequest req, HttpResponse resp) {
        String user = (String) req.getRawRequest().getAttribute("authUser");

        return "Bem-vindo ao painel admin, " + user + "!";
    }

    @Route(path = "/logout", method = "GET")
    public String logout(HttpRequest req, HttpResponse resp) {
        // Remove o cookie de autenticação
        Cookie cookie = new Cookie("authToken", "");
        cookie.setMaxAge(0); // Expira imediatamente
        cookie.setPath("/"); // Aplica para todo o site
        resp.getRawResponse().addCookie(cookie);

        return "Logout realizado com sucesso!";
    }

}
