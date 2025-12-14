package cv.ntinnovation.vintipay.adapter.in.web.handler;

import cv.ntinnovation.vintipay.adapter.in.web.security.CustomOAuth2User;
import cv.ntinnovation.vintipay.adapter.out.persistence.entity.PapelEntity;
import cv.ntinnovation.vintipay.adapter.out.persistence.entity.UsuarioEntity;
import cv.ntinnovation.vintipay.adapter.out.persistence.repository.PapelRepository;
import cv.ntinnovation.vintipay.adapter.out.persistence.repository.UsuarioRepository;
import cv.ntinnovation.vintipay.application.core.domain.entity.Papel;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.core.service.TenentService;
import cv.ntinnovation.vintipay.application.port.out.BuscaUsuario;
import cv.ntinnovation.vintipay.application.port.out.BuscaUsuarioPort;
import cv.ntinnovation.vintipay.application.port.out.GerenciaPapelPort;
import cv.ntinnovation.vintipay.application.port.out.SalvaUsuarioPort;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final SalvaUsuarioPort salvaUsuario;
    private final BuscaUsuario buscaUsuario;
    private final GerenciaPapelPort papelPort;
    private final TenentService tenentService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        log.info("Autenticando usuario handler: {}", authentication.getPrincipal());
        if (authentication.getPrincipal() instanceof CustomOAuth2User customUser) {
            Usuario usuario = customUser.getUsuario();
            
            log.info("Autenticação OAuth2 bem-sucedida para: {} (ID: {})", usuario.getEmail(), usuario.getId());
            
            usuario.setUltimoLogin(LocalDateTime.now());
            salvaUsuario.salvar(usuario);
            
            String targetUrl = determinarUrlRedirecionamento(authentication);
            response.sendRedirect(targetUrl);
        } else if (authentication.getPrincipal() instanceof OidcUser oidcUser) {

            String email = oidcUser.getEmail();
            String username = oidcUser.getPreferredUsername();
            String nome = oidcUser.getFullName();
            
            // Extrair array de tenant slugs dos claims do OIDC
            List<String> tenantSlugs = extrairTenantSlugs(oidcUser);
            
            log.info("Autenticação OAuth2 bem-sucedida para: {} | Tenants extraídos do scope: {}", email, tenantSlugs);
            
            Usuario usuario = buscaUsuario.buscarPorEmail(email)
                    .orElseGet(() -> criarUsuarioDoKeycloak(oidcUser, email, username, nome));
            
            // Processar e associar tenants ao usuário
            if (!tenantSlugs.isEmpty()) {
                tenentService.processarEAssociarTenants(usuario, tenantSlugs);
                // Selecionar o primeiro tenant na sessão
                tenentService.selecionarTenant(request, tenantSlugs.get(0));
            }
            
            usuario.setUltimoLogin(LocalDateTime.now());
            usuario.setEmailVerified(oidcUser.getEmailVerified());
            salvaUsuario.salvar(usuario);
            
            log.info("Usuário sincronizado: {} (ID: {})", usuario.getEmail(), usuario.getId());
            
            String targetUrl = determinarUrlRedirecionamento(authentication);
            response.sendRedirect(targetUrl);
        } else {
            response.sendRedirect("/dashboard");
        }
    }

    private Usuario criarUsuarioDoKeycloak(OidcUser oidcUser, String email, String username, String nome) {
        log.info("Criando novo usuário a partir do Keycloak: {}", email);
        
        Set<Papel> papeis = new HashSet<>();
        
        for (GrantedAuthority authority : oidcUser.getAuthorities()) {
            String authorityName = authority.getAuthority();
            
            if (authorityName.startsWith("ROLE_")) {
                String roleName = authorityName.substring(5);
                
                Papel papel = papelPort.buscarPorCodigo(roleName)
                        .orElseGet(() -> criarPapelSeNaoExistir(roleName));
                
                papeis.add(papel);
                log.info("Papel atribuído: {}", roleName);
            }
        }
        
        if (papeis.isEmpty()) {
            Papel papelPadrao = papelPort.buscarPapelPadrao()
                    .orElseGet(() -> papelPort.buscarPorCodigo("USER")
                            .orElseGet(() -> criarPapelSeNaoExistir("USER")));
            papeis.add(papelPadrao);
            log.info("Nenhum papel encontrado, usando papel padrão: {}", papelPadrao.getCodigo());
        }
        
        Usuario novoUsuario =  Usuario.builder(
                username != null ? username : email,
                email,
                nome,
                "",
                true,
                papeis.stream().map(Papel::getCodigo).toArray(String[]::new),
                papeis,
                new HashSet<>()
                );

                novoUsuario.setEmailVerified(oidcUser.getEmailVerified());
                novoUsuario.setDataRegistro(LocalDateTime.now());
                novoUsuario.setUltimoLogin(LocalDateTime.now());

        return salvaUsuario.salvar(novoUsuario);
    }

    private Papel criarPapelSeNaoExistir(String codigo) {
        log.info("Criando novo papel: {}", codigo);
        
        Papel novoPapel = Papel.builder()
                .codigo(codigo)
                .nome(codigo)
                .descricao("Papel importado do Keycloak: " + codigo)
                .padrao(codigo.equals("USER"))
                .permissoes(new HashSet<>())
                .usuarios(new HashSet<>())
                .build();
        
        return papelPort.salvar(novoPapel);
    }

    private List<String> extrairTenantSlugs(OidcUser oidcUser) {
        List<String> slugs = new ArrayList<>();
        
        // Tentar extrair de diferentes claims possíveis
        Object tenantsClaim = null;
        
        if (oidcUser.getClaims().containsKey("tenants")) {
            tenantsClaim = oidcUser.getClaim("tenants");
        } else if (oidcUser.getClaims().containsKey("organizations")) {
            tenantsClaim = oidcUser.getClaim("organizations");
        } else if (oidcUser.getClaims().containsKey("tenant_slugs")) {
            tenantsClaim = oidcUser.getClaim("tenant_slugs");
        }
        
        if (tenantsClaim != null) {
            // Se for uma lista/array
            if (tenantsClaim instanceof List<?> lista) {
                for (Object item : lista) {
                    if (item != null) {
                        slugs.add(item.toString());
                    }
                }
            }
            // Se for uma string separada por vírgula ou espaço
            else if (tenantsClaim instanceof String tenantsStr) {
                String[] partes = tenantsStr.split("[,\\s]+");
                for (String parte : partes) {
                    if (!parte.trim().isEmpty()) {
                        slugs.add(parte.trim());
                    }
                }
            }
        }
        
        return slugs;
    }

    private String determinarUrlRedirecionamento(Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        if (isAdmin) {
            return "/admin/";
        }


        
        return "/user/";
    }
}
