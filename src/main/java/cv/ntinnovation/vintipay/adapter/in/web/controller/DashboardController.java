package cv.ntinnovation.vintipay.adapter.in.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.stream.Collectors;

@Slf4j
@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboardRedirect() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null) {
            log.info("=== DASHBOARD REDIRECT DEBUG ===");
            log.info("Principal: {}", auth.getPrincipal().getClass().getName());
            log.info("Authorities: {}", auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(", ")));
            log.info("Is Authenticated: {}", auth.isAuthenticated());
            
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            
            log.info("Has ROLE_ADMIN: {}", isAdmin);
            
            if (isAdmin) {
                log.info("Redirecting to /admin/");
                return "redirect:/admin/";
            }
            
            log.info("Redirecting to /user/");
            return "redirect:/user/";
        }
        
        log.warn("No authentication found, redirecting to /user/");
        return "redirect:/user/";
    }
}
