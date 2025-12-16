package cv.ntinnovation.vintipay.application.core.service;

import cv.ntinnovation.vintipay.adapter.out.client.api.verifika.VerifikaAdapter;
import cv.ntinnovation.vintipay.application.core.domain.entity.Tenant;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.port.out.GerenciaTenantPort;
import cv.ntinnovation.vintipay.application.port.out.SalvaUsuarioPort;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class TenentService {
    private static final String SESSION_TENANT_KEY = "tenant_selecionado";
    
    private final GerenciaTenantPort tenantPort;
    private final SalvaUsuarioPort usuarioPort;
    private final VerifikaAdapter keycloakAdminAdapter;

    public void selecionarTenant(HttpServletRequest request, String tenantIdentifier) {
        if (tenantIdentifier == null || tenantIdentifier.trim().isEmpty()) {
            log.warn("Tentativa de selecionar tenant com identificador vazio ou nulo");
            return;
        }
        
        HttpSession session = request.getSession(true);
        session.setAttribute(SESSION_TENANT_KEY, tenantIdentifier);
        log.info("Tenant selecionado na sessão: {}", tenantIdentifier);
    }
    
    public String obterTenantAtual(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (String) session.getAttribute(SESSION_TENANT_KEY);
    }
    
    public void limparTenant(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(SESSION_TENANT_KEY);
            log.info("Tenant removido da sessão");
        }
    }
    
    @Transactional
    public void processarEAssociarTenants(Usuario usuario, List<String> tenantSlugs) {
        if (tenantSlugs == null || tenantSlugs.isEmpty()) {
            log.warn("Lista de tenant slugs vazia para usuário: {}", usuario.getEmail());
            return;
        }
        
        List<Tenant> tenantsUsuario = tenantPort.buscarPorSlugIn(tenantSlugs);

        usuario.getTenants().clear();
        usuario.getTenants().addAll(tenantsUsuario);
        usuarioPort.salvar(usuario);
        
        log.info("Associados {} tenants ao usuário {}: {}", 
                tenantsUsuario.size(), usuario.getEmail(), tenantSlugs);
    }

    /**
     * Atualiza o atributo 'tenants' no perfil do usuário no Keycloak
     */
    private void atualizarTenantsNoKeycloak(String email, List<String> tenantSlugs) {
        try {
            Map<String, List<String>> atributos = new HashMap<>();
            atributos.put("tenants", tenantSlugs);
            
            boolean sucesso = keycloakAdminAdapter.atualizarAtributos(email, atributos);
            
            if (sucesso) {
                log.info("Atributo 'tenants' atualizado no Keycloak para usuário {}: {}", email, tenantSlugs);
            } else {
                log.warn("Não foi possível atualizar atributo 'tenants' no Keycloak para usuário {}", email);
            }
        } catch (Exception e) {
            log.error("Erro ao atualizar atributo 'tenants' no Keycloak: {}", e.getMessage(), e);
        }
    }
    
    private String gerarNomeDoSlug(String slug) {
        String[] palavras = slug.split("[_-]");
        StringBuilder nome = new StringBuilder();
        
        for (String palavra : palavras) {
            if (!palavra.isEmpty()) {
                nome.append(Character.toUpperCase(palavra.charAt(0)))
                    .append(palavra.substring(1).toLowerCase())
                    .append(" ");
            }
        }
        
        return nome.toString().trim();
    }
}
