package cv.ntinnovation.vintipay.adapter.in.web.config;

import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import cv.ntinnovation.vintipay.adapter.in.web.handler.CustomAuthenticationFailureHandler;
import cv.ntinnovation.vintipay.adapter.in.web.handler.OAuth2AuthenticationSuccessHandler;
import cv.ntinnovation.vintipay.adapter.out.persistence.repository.UsuarioRepository;
import cv.ntinnovation.vintipay.adapter.out.persistence.repository.PapelRepository;
import cv.ntinnovation.vintipay.application.port.in.SessionAuthenticationUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
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
        private final UsuarioRepository usuarioRepository;
        private final PapelRepository papelRepository;

        @Bean
        public AuthenticationManager authManager(HttpSecurity http, SessionAuthenticationUseCase sessionAuthenticationProvider) throws Exception {
            return http.getSharedObject(AuthenticationManagerBuilder.class)
                    .authenticationProvider(sessionAuthenticationProvider) // Registra o provedor personalizado
                    .build();
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {

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
                                    .oidcUserService(oidcUserService(usuarioRepository, papelRepository))
                            )
                    )
                    .logout(logout -> logout
                            .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                            .logoutSuccessUrl("/login?logout")
                            .invalidateHttpSession(true)
                            .clearAuthentication(true)
                            .deleteCookies("JSESSIONID")
                            .permitAll()
                    )
                    .csrf(csrf -> csrf
                            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                            .ignoringRequestMatchers("/admin/**","/user/**","/p/**")
                    )
                    .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));

            return http.build();
        }

        @Bean
        public OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService(UsuarioRepository usuarioRepository, PapelRepository papelRepository) {
            final OidcUserService delegate = new OidcUserService();

            return (userRequest) -> {
                OidcUser oidcUser = delegate.loadUser(userRequest);

                log.info("=== OIDC USER DEBUG ===");
                log.info("User: {}", oidcUser);
                log.info("Authorities: {}", oidcUser.getAuthorities());
                log.info("Email: {}", oidcUser.getEmail());
                log.info("Name: {}", oidcUser.getName());
                log.info("Userinfo: {}", oidcUser.getUserInfo());
                log.info("IdToken: {}", oidcUser.getIdToken());

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
                            .collect(Collectors.toList()));
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
                                        .collect(Collectors.toList()));
                            }
                        }
                    });
                }

                // Adicionar authorities existentes
                mappedAuthorities.addAll(oidcUser.getAuthorities());

                OidcUser oidcUserWithRoles = new DefaultOidcUser(new ArrayList<>(mappedAuthorities), oidcUser.getIdToken(), oidcUser.getUserInfo());
                
                String email = oidcUser.getEmail();
                cv.ntinnovation.vintipay.adapter.out.persistence.entity.UsuarioEntity usuario = usuarioRepository.findByEmail(email).orElse(null);
                
                if (usuario != null) {
                    return new cv.ntinnovation.vintipay.adapter.in.web.security.CustomOAuth2User(oidcUserWithRoles, usuario);
                }
                
                return oidcUserWithRoles;
            };
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
                        .collect(Collectors.toList()));
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
                                    .collect(Collectors.toList()));
                        }
                    }
                });
            }

            return authorities;
        });

        return jwtAuthenticationConverter;
    }

}
