package cv.ntinnovation.vintipay.adapter.in.web.handler;

import cv.ntinnovation.vintipay.adapter.in.web.security.CustomOAuth2User;
import cv.ntinnovation.vintipay.adapter.out.persistence.entity.PapelEntity;
import cv.ntinnovation.vintipay.adapter.out.persistence.entity.UsuarioEntity;
import cv.ntinnovation.vintipay.adapter.out.persistence.repository.PapelRepository;
import cv.ntinnovation.vintipay.adapter.out.persistence.repository.UsuarioRepository;
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
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UsuarioRepository usuarioRepository;
    private final PapelRepository papelRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        if (authentication.getPrincipal() instanceof CustomOAuth2User) {
            CustomOAuth2User customUser = (CustomOAuth2User) authentication.getPrincipal();
            UsuarioEntity usuario = customUser.getUsuario();
            
            log.info("Autenticação OAuth2 bem-sucedida para: {} (ID: {})", usuario.getEmail(), usuario.getId());
            
            usuario.setUltimoLogin(LocalDateTime.now());
            usuarioRepository.save(usuario);
            
            String targetUrl = determinarUrlRedirecionamento(authentication);
            response.sendRedirect(targetUrl);
        } else if (authentication.getPrincipal() instanceof OidcUser) {
            OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
            
            String email = oidcUser.getEmail();
            String username = oidcUser.getPreferredUsername();
            String nome = oidcUser.getFullName();
            
            log.info("Autenticação OAuth2 bem-sucedida para: {}", email);
            
            UsuarioEntity usuario = usuarioRepository.findByEmail(email)
                    .orElseGet(() -> criarUsuarioDoKeycloak(oidcUser, email, username, nome));
            
            usuario.setUltimoLogin(LocalDateTime.now());
            usuario.setEmailVerified(oidcUser.getEmailVerified());
            usuarioRepository.save(usuario);
            
            log.info("Usuário sincronizado: {} (ID: {})", usuario.getEmail(), usuario.getId());
            
            String targetUrl = determinarUrlRedirecionamento(authentication);
            response.sendRedirect(targetUrl);
        } else {
            response.sendRedirect("/dashboard");
        }
    }

    private UsuarioEntity criarUsuarioDoKeycloak(OidcUser oidcUser, String email, String username, String nome) {
        log.info("Criando novo usuário a partir do Keycloak: {}", email);
        
        Set<PapelEntity> papeis = new HashSet<>();
        
        for (GrantedAuthority authority : oidcUser.getAuthorities()) {
            String authorityName = authority.getAuthority();
            
            if (authorityName.startsWith("ROLE_")) {
                String roleName = authorityName.substring(5);
                
                PapelEntity papel = papelRepository.findByCodigo(roleName)
                        .orElseGet(() -> criarPapelSeNaoExistir(roleName));
                
                papeis.add(papel);
                log.info("Papel atribuído: {}", roleName);
            }
        }
        
        if (papeis.isEmpty()) {
            PapelEntity papelPadrao = papelRepository.findByPadraoTrue()
                    .orElseGet(() -> papelRepository.findByCodigo("USER")
                            .orElseGet(() -> criarPapelSeNaoExistir("USER")));
            papeis.add(papelPadrao);
            log.info("Nenhum papel encontrado, usando papel padrão: {}", papelPadrao.getCodigo());
        }
        
        UsuarioEntity novoUsuario = UsuarioEntity.builder()
                .username(username != null ? username : email)
                .email(email)
                .nome(nome)
                .password("")
                .role(papeis.iterator().next().getCodigo())
                .papeis(papeis)
                .ativo(true)
                .emailVerified(oidcUser.getEmailVerified())
                .dataRegistro(LocalDateTime.now())
                .ultimoLogin(LocalDateTime.now())
                .build();
        
        return usuarioRepository.save(novoUsuario);
    }

    private PapelEntity criarPapelSeNaoExistir(String codigo) {
        log.info("Criando novo papel: {}", codigo);
        
        PapelEntity novoPapel = PapelEntity.builder()
                .codigo(codigo)
                .nome(codigo)
                .descricao("Papel importado do Keycloak: " + codigo)
                .padrao(codigo.equals("USER"))
                .permissoes(new HashSet<>())
                .usuarios(new HashSet<>())
                .build();
        
        return papelRepository.save(novoPapel);
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
