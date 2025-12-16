package cv.ntinnovation.vintipay.adapter.in.web.handler;

import cv.ntinnovation.vintipay.adapter.in.web.security.CustomOAuth2User;
import cv.ntinnovation.vintipay.application.core.domain.entity.Tenant;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.port.out.BuscaUsuario;
import cv.ntinnovation.vintipay.application.port.out.GerenciaTenantPort;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class VerificationInterceptorHandler implements HandlerInterceptor {

    private final BuscaUsuario buscaUsuario;
    private final GerenciaTenantPort gerenciaTenantPort;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        HttpSession session = request.getSession();

        String requestURI = request.getRequestURI();

        log.info("Request URI: {}", requestURI);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() instanceof CustomOAuth2User customUser) {
            Usuario usuario = buscaUsuario.buscarPorEmail(customUser.getEmail()).orElse(null);

            assert usuario != null;

            List<Tenant> tenants = gerenciaTenantPort.buscarTenantPorUsuario(usuario.getId());

            log.info("User (if): {}", tenants);
            if(tenants == null || tenants.isEmpty()) {
                response.sendRedirect("/user/tenant/create");
                return false;
            }
        }  else {
            log.info("User (else): {}", authentication.getPrincipal());
            throw new RuntimeException("Usuario nao encontrado");
        }

        return true; // segue a execução normal
    }
}
