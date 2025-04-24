package cv.ntinnovation.vintipay.adapter.in.web.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador para o redirecionamento do dashboard
 * Redireciona os usuários para o dashboard apropriado com base em seu papel
 */
@Controller
public class DashboardController {

    /**
     * Redireciona para o dashboard apropriado com base no papel do usuário
     * @return Redirecionamento para o dashboard de admin ou usuário
     */
    @GetMapping("/dashboard")
    public String dashboardRedirect() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        // Verifica se o usuário tem papel de ADMIN
        if (auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            // Redireciona para o dashboard de administrador
            return "redirect:/admin/";
        }
        
        // Redireciona para o dashboard de usuário padrão
        return "redirect:/user/";
    }
}
