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

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class VerificationInterceptorHandler implements HandlerInterceptor {

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
            Usuario usuario = customUser.getUsuario();

            assert usuario != null;

            List<Tenant> tenants = gerenciaTenantPort.buscarTenantPorUsuario(usuario.getId());

            log.info("User (if): {}", tenants);
            if(tenants.isEmpty()) {
                response.sendRedirect("/user/tenant/create");
                return false;
            }

            // Só define tenant da sessão se ainda não houver um definido
            if(session.getAttribute("tenantId") == null || session.getAttribute("tenantSlug") == null) {
                List<String> tenantSlugs = customUser.getTenantSlugs();
                
                // Tenta usar tenant dos claims do OAuth2
                tenants.stream()
                    .filter(tenant -> tenantSlugs.contains(tenant.getSlug()))
                    .findFirst()
                    .ifPresentOrElse(
                        tenant -> {
                            session.setAttribute("tenantId", tenant.getId());
                            session.setAttribute("tenantSlug", tenant.getSlug());
                            log.info("Tenant definido da sessão OAuth2: {}", tenant.getSlug());
                        },
                        () -> {
                            // Se não houver tenant nos claims, usa o primeiro disponível
                            if(!tenants.isEmpty()) {
                                Tenant firstTenant = tenants.get(0);
                                session.setAttribute("tenantId", firstTenant.getId());
                                session.setAttribute("tenantSlug", firstTenant.getSlug());
                                log.info("Tenant padrão definido: {}", firstTenant.getSlug());
                            }
                        }
                    );
            } else {
                log.info("Tenant já definido na sessão: {}", session.getAttribute("tenantSlug"));
            }

            usuario.setTenants(new HashSet<>(tenants));

        }  else {
            log.info("User (else): {}", authentication.getPrincipal());
            throw new RuntimeException("Usuario nao encontrado");
        }

        return true; // segue a execução normal
    }
}
