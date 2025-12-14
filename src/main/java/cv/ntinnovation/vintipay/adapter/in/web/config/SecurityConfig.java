package cv.ntinnovation.vintipay.adapter.in.web.config;

import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import cv.ntinnovation.vintipay.adapter.in.web.handler.CustomAuthenticationFailureHandler;
import cv.ntinnovation.vintipay.adapter.in.web.handler.OAuth2AuthenticationSuccessHandler;
import cv.ntinnovation.vintipay.adapter.in.web.security.CustomOAuth2User;
import cv.ntinnovation.vintipay.adapter.out.persistence.repository.UsuarioRepository;
import cv.ntinnovation.vintipay.adapter.out.persistence.repository.PapelRepository;
import cv.ntinnovation.vintipay.application.core.domain.entity.Papel;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.core.service.TenentService;
import cv.ntinnovation.vintipay.application.port.in.SessionAuthenticationUseCase;
import cv.ntinnovation.vintipay.application.port.out.BuscaUsuario;
import cv.ntinnovation.vintipay.application.port.out.GerenciaPapelPort;
import cv.ntinnovation.vintipay.application.port.out.SalvaUsuarioPort;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import static org.springframework.security.config.Customizer.withDefaults;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${jwt.public.key}")
    private RSAPublicKey publicKey;
    @Value("${jwt.private.key}")
    private RSAPrivateKey privateKey;

    @Configuration
    @Order(1)
    public static class ApiSecurityConfig {
        @Bean
        public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {

            http.securityMatcher("/api/**")
                    .authorizeHttpRequests((authorize) ->
                            authorize.requestMatchers(HttpMethod.POST,"/api/v1/authorize").permitAll()
                                    .requestMatchers("/api/location/**").permitAll()
                                    .requestMatchers("/api/location-data/**").permitAll()
                                    .requestMatchers("/api/pagamento-publico/**").permitAll()
                                    .anyRequest().authenticated())
                    .oauth2ResourceServer((oauth2) -> {
                        oauth2.jwt(withDefaults()).bearerTokenResolver(bearerTokenResolver());
                    })
                    .csrf(AbstractHttpConfigurer::disable)
                    .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

            return http.build();
        }

        @Bean
        public SecurityFilterChain webhookSecurityFilterChain(HttpSecurity http) throws Exception {

            http.securityMatcher("/webhook/**")
                    .authorizeHttpRequests((authorize) ->
                            authorize.requestMatchers(HttpMethod.POST,"/webhook/sisp/pagamentos/retorno/{reference}").permitAll()
                                    .requestMatchers(HttpMethod.POST,"/webhook/sisp/estorno/retorno/{reference}").permitAll()
                                    .requestMatchers("/webhook/stripe").permitAll()
                                    .anyRequest().authenticated())
                    .oauth2ResourceServer((oauth2) -> {
                        oauth2.jwt(withDefaults()).bearerTokenResolver(bearerTokenResolver());
                    })
                    .csrf(cookie -> cookie.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                            .ignoringRequestMatchers("/webhook/sisp/pagamentos/retorno/{reference}", "/webhook/sisp/estorno/retorno/{reference}", "/webhook/stripe"))
                    .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

            return http.build();
        }

        @Bean
        BearerTokenResolver bearerTokenResolver() {
            DefaultBearerTokenResolver bearerTokenResolver = new DefaultBearerTokenResolver();
            bearerTokenResolver.setBearerTokenHeaderName(HttpHeaders.AUTHORIZATION);
            return bearerTokenResolver;
        }
    }

    @Configuration
    @Order(2)
    @RequiredArgsConstructor
    public static class WebSecurityConfig {

        private final CustomAuthenticationFailureHandler authenticationFailureHandler;
        private final OAuth2AuthenticationSuccessHandler oauth2AuthenticationSuccessHandler;
        private final BuscaUsuario buscaUsuario;
        private final GerenciaPapelPort gerenciaPapelPort;
        private final SalvaUsuarioPort salvaUsuario;
        private final TenentService tenentService;
        private final HttpServletRequest request;

        @Bean
        public AuthenticationManager authManager(HttpSecurity http, SessionAuthenticationUseCase sessionAuthenticationProvider) throws Exception {
            return http.getSharedObject(AuthenticationManagerBuilder.class)
                    .authenticationProvider(sessionAuthenticationProvider) // Registra o provedor personalizado
                    .build();
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http, ClientRegistrationRepository clientRegistrationRepository) throws Exception {

            http.securityMatcher("/**")
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/").permitAll()
                            .requestMatchers("/privacidade").permitAll()
                            .requestMatchers("/termos").permitAll()
                            .requestMatchers("/admin/**").hasRole("ADMIN")
                            .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                            .requestMatchers("/dashboard", "/dashboard/**").hasAnyRole("USER", "ADMIN")
                            .requestMatchers("/permissoes/**").hasRole("ADMIN")
                            .requestMatchers("/papeis/**").hasRole("ADMIN")
                            .requestMatchers("/pagamento/checkout/{reference}").permitAll()
                            .requestMatchers("/subscricao/checkout/{reference}").permitAll()
                            .requestMatchers("/recibo/download/pdf/**").permitAll()
                            .requestMatchers("/recibo/enviar-email").permitAll()
                            .requestMatchers("/recibo/view/**").permitAll()
                            .requestMatchers("/recibo/api/qrcode/**").permitAll()
                            .requestMatchers("/error").permitAll()
                            .requestMatchers("/login/**").permitAll()
                            .requestMatchers("/oauth2/**").permitAll()
                            .requestMatchers("/verificar-email").permitAll()
                            .requestMatchers(HttpMethod.POST,"/reenviar-verificacao").permitAll()
                            .requestMatchers(HttpMethod.GET,"/reenviar-verificacao").permitAll()
                            .requestMatchers("/registro").permitAll()
                            .requestMatchers("/recuperar-senha").permitAll()
                            .requestMatchers("/recuperar-senha/confirmacao").permitAll()
                            .requestMatchers("/redefinir-senha").permitAll()
                            .requestMatchers(HttpMethod.POST, "/p/**").permitAll()
                            .requestMatchers(HttpMethod.GET, "/p/**").permitAll()
                            .requestMatchers("/registro/checkout-success").permitAll()
                            .requestMatchers("/registro/checkout-cancel").permitAll()
                            .requestMatchers(HttpMethod.POST, "/login").permitAll()
                            .requestMatchers(HttpMethod.POST, "/registro").permitAll()
                            .requestMatchers("/images/**", "/css/**", "/js/**", "/img/**", "/uploads/**").permitAll()
                            .anyRequest().authenticated()
                    )
                    // OAuth2 Login com Keycloak
                    .oauth2Login(oauth2 -> oauth2
                            .loginPage("/login")
                            .successHandler(oauth2AuthenticationSuccessHandler)
                            .defaultSuccessUrl("/dashboard", true)
                            .userInfoEndpoint(userInfo -> userInfo
                                    .oidcUserService(oidcUserService(request, salvaUsuario, buscaUsuario, gerenciaPapelPort, tenentService))
                            )
                    )
                    .logout(logout -> logout
                            .logoutSuccessHandler(oidcLogoutSuccessHandler(clientRegistrationRepository))
                            .invalidateHttpSession(true)
                            .clearAuthentication(true)
                            .deleteCookies("JSESSIONID")
                            .logoutSuccessUrl("/login?logout")
                            .permitAll()
                    )
                    .csrf(csrf -> csrf
                            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                            .ignoringRequestMatchers("/admin/**","/user/**","/p/**")
                    )
                    .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));

            return http.build();
        }

        private LogoutSuccessHandler oidcLogoutSuccessHandler(ClientRegistrationRepository clientRegistrationRepository) {
            OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler =
                    new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
            oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}/login?logout");
            return oidcLogoutSuccessHandler;
        }

        @Bean
        public OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService(HttpServletRequest request, SalvaUsuarioPort salvaUsuario, BuscaUsuario buscaUsuario, GerenciaPapelPort gerenciaPapelPort, TenentService tenentService) {
            final OidcUserService delegate = new OidcUserService();

            return (userRequest) -> {

                OidcUser oidcUser = delegate.loadUser(userRequest);

                // Extrair roles do Keycloak
                Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
                
                // Roles do realm
                var realmAccess = oidcUser.getClaimAsMap("realm_access");
                log.info("Realm Access: {}", realmAccess);
                if (realmAccess != null && realmAccess.containsKey("roles")) {
                    @SuppressWarnings("unchecked")
                    Collection<String> roles = (Collection<String>) realmAccess.get("roles");
                    mappedAuthorities.addAll(roles.stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                            .toList());
                }

                // Roles do resource (client)
                var resourceAccess = oidcUser.getClaimAsMap("resource_access");
                if (resourceAccess != null) {
                    resourceAccess.values().forEach(resource -> {
                        if (resource instanceof Map) {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> resourceMap = (Map<String, Object>) resource;
                            if (resourceMap.containsKey("roles")) {
                                @SuppressWarnings("unchecked")
                                Collection<String> roles = (Collection<String>) resourceMap.get("roles");
                                mappedAuthorities.addAll(roles.stream()
                                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                                        .toList());
                            }
                        }
                    });
                }

                // Adicionar authorities existentes
                mappedAuthorities.addAll(oidcUser.getAuthorities());

                OidcUser oidcUserWithRoles = new DefaultOidcUser(new ArrayList<>(mappedAuthorities), oidcUser.getIdToken(), oidcUser.getUserInfo());
                
                String email = oidcUser.getEmail();
                String username = oidcUser.getPreferredUsername();
                String nome = oidcUser.getFullName();

                Usuario usuario =  buscaUsuario.buscarPorEmail(email)
                        .orElseGet(() -> criarUsuarioDoKeycloak(oidcUser, email, username, nome));

                // Extrair array de tenant slugs dos claims do OIDC
                List<String> tenantSlugs = extrairTenantSlugs(oidcUser);

                // Processar e associar tenants ao usuário
                if (!tenantSlugs.isEmpty()) {
                    tenentService.processarEAssociarTenants(usuario, tenantSlugs);
                    tenentService.selecionarTenant(request, tenantSlugs.get(0));
                }

                if (usuario != null) {
                    return new CustomOAuth2User(oidcUserWithRoles, usuario);
                }

                return oidcUserWithRoles;
            };
        }

        private Usuario criarUsuarioDoKeycloak(OidcUser oidcUser, String email, String username, String nome) {
            log.info("Criando novo usuário a partir do Keycloak: {}", email);

            Set<Papel> papeis = new HashSet<>();

            for (GrantedAuthority authority : oidcUser.getAuthorities()) {
                String authorityName = authority.getAuthority();

                if (authorityName.startsWith("ROLE_")) {
                    String roleName = authorityName.substring(5);

                    Papel papel = gerenciaPapelPort.buscarPorCodigo(roleName)
                            .orElseGet(() -> criarPapelSeNaoExistir(roleName));

                    papeis.add(papel);
                    log.info("Papel atribuído: {}", roleName);
                }
            }

            if (papeis.isEmpty()) {
                Papel papelPadrao = gerenciaPapelPort.buscarPapelPadrao()
                        .orElseGet(() -> gerenciaPapelPort.buscarPorCodigo("USER")
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

            return gerenciaPapelPort.salvar(novoPapel);
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

        @Bean
        public static PasswordEncoder encoder() {
            return new BCryptPasswordEncoder();
        }

    }

    @Configuration
    @Order(3)
    public static class SimulatorSecurityConfig {

        @Bean
        public SecurityFilterChain simulatorSecurityFilterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {

            http.securityMatcher("/simulador/**")
                    .csrf(csrf -> csrf
                            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                            .ignoringRequestMatchers("/simulador/**")
                    )
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers(HttpMethod.POST, "/simulador/**").permitAll()
                            .requestMatchers(HttpMethod.GET, "/simulador/**").permitAll()
                            .requestMatchers("/images/**", "/css/**", "/js/**", "/img/**", "/uploads/**").permitAll()
                            .anyRequest().authenticated()
                    )
                    .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                            .maximumSessions(1));

            return http.build();
        }

    }


    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        JWK jwk = new RSAKey.Builder(this.publicKey).privateKey(this.privateKey).build();
        var jwks = new ImmutableJWKSet<>(new JWKSet(jwk));

        return new NimbusJwtEncoder(jwks);
    }

    @Bean
    public JWSVerifier jwtJWSVerifier() {
        return new RSASSAVerifier((RSAPublicKey) publicKey);
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            
            // Extrair roles do realm_access
            Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
            if (realmAccess != null && realmAccess.containsKey("roles")) {
                @SuppressWarnings("unchecked")
                Collection<String> roles = (Collection<String>) realmAccess.get("roles");
                authorities.addAll(roles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                        .toList());
            }

            // Extrair roles do resource_access
            Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");
            if (resourceAccess != null) {
                resourceAccess.values().forEach(resource -> {
                    if (resource instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> resourceMap = (Map<String, Object>) resource;
                        if (resourceMap.containsKey("roles")) {
                            @SuppressWarnings("unchecked")
                            Collection<String> roles = (Collection<String>) resourceMap.get("roles");
                            authorities.addAll(roles.stream()
                                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                                    .toList());
                        }
                    }
                });
            }

            return authorities;
        });

        return jwtAuthenticationConverter;
    }

}
