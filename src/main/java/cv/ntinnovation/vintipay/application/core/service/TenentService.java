package cv.ntinnovation.vintipay.application.core.service;

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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class TenentService {
    private static final String SESSION_TENANT_KEY = "tenant_selecionado";
    
    private final GerenciaTenantPort tenantPort;
    private final SalvaUsuarioPort usuarioPort;

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
        
        Set<Tenant> tenantsUsuario = new HashSet<>();
        
        for (String slug : tenantSlugs) {
            if (slug == null || slug.trim().isEmpty()) {
                continue;
            }
            
            Tenant tenant = tenantPort.buscarPorSlug(slug.trim())
                    .orElseGet(() -> criarTenantPorSlug(slug.trim()));
            
            tenantsUsuario.add(tenant);
        }
        
        usuario.getTenants().clear();
        usuario.getTenants().addAll(tenantsUsuario);
        usuarioPort.salvar(usuario);
        
        log.info("Associados {} tenants ao usuário {}: {}", 
                tenantsUsuario.size(), usuario.getEmail(), tenantSlugs);
    }
    
    private Tenant criarTenantPorSlug(String slug) {
        log.info("Criando novo tenant com slug: {}", slug);
        
        Tenant tenant = Tenant.builder()
                .codigo(slug.toUpperCase())
                .slug(slug)
                .nome(gerarNomeDoSlug(slug))
                .ativo(true)
                .createdAt(LocalDateTime.now())
                .build();
        
        return tenantPort.salvar(tenant);
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
