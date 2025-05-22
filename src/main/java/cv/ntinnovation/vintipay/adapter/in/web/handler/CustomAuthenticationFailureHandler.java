package cv.ntinnovation.vintipay.adapter.in.web.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Handler personalizado para falhas de autenticação, com foco especial
 * no tratamento da exceção DisabledException que ocorre quando um usuário
 * tenta fazer login com um email não verificado.
 */
@Slf4j
@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, 
                                        AuthenticationException exception) throws IOException, ServletException {
        String redirectUrl = "/login?error";
        System.out.println(exception.getMessage());
        // Se for uma exceção de email não verificado, use um parâmetro específico
        if (exception instanceof DisabledException && 
                exception.getMessage() != null && 
                exception.getMessage().contains("Email não verificado")) {
            
            String username = request.getParameter("username");
            redirectUrl = "/login?email_not_verified=true";
            
            if (username != null && !username.isEmpty()) {
                redirectUrl += "&username=" + username;
            }
            
            log.info("Usuário com username não verificado tentou fazer login: {}", username);
        } else {
            log.info("Falha de autenticação: {}", exception.getMessage());
        }
        
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
