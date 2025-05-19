package cv.ntinnovation.vintipay.adapter.in.web.config;

import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import cv.ntinnovation.vintipay.application.port.in.SessionAuthenticationUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
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
                                    .requestMatchers("/webhook/stripe").permitAll()
                                    .anyRequest().authenticated())
                    .oauth2ResourceServer((oauth2) -> {
                        oauth2.jwt(withDefaults()).bearerTokenResolver(bearerTokenResolver());
                    })
                    .csrf(cookie -> cookie.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                            .ignoringRequestMatchers("/webhook/sisp/pagamentos/retorno/{reference}", "/webhook/stripe"))
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
    public static class WebSecurityConfig {

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
                            .requestMatchers("/admin/**").hasRole("ADMIN")
                            .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                            .requestMatchers("/permissoes/**").hasRole("ADMIN")
                            .requestMatchers("/papeis/**").hasRole("ADMIN")
                            .requestMatchers("/pagamento/checkout/{reference}").permitAll()
                            .requestMatchers("/recibo/download/pdf/**").permitAll()
                            .requestMatchers("/recibo/enviar-email").permitAll()
                            .requestMatchers("/recibo/api/qrcode/**").permitAll()
                            .requestMatchers("/error").permitAll()
                            .requestMatchers("/login").permitAll()
                            .requestMatchers("/registro").permitAll()
                            .requestMatchers(HttpMethod.POST, "/p/**").permitAll()
                            .requestMatchers(HttpMethod.GET, "/p/**").permitAll()
                            .requestMatchers("/registro/checkout-success").permitAll()
                            .requestMatchers("/registro/checkout-cancel").permitAll()
                            .requestMatchers(HttpMethod.POST, "/login").permitAll()
                            .requestMatchers(HttpMethod.POST, "/registro").permitAll()
                            .requestMatchers("/images/**", "/css/**", "/js/**", "/img/**", "/uploads/**").permitAll()
                            .anyRequest().authenticated()
                    )
                    .formLogin(form -> form
                            .loginPage("/login")
                            .loginProcessingUrl("/login")
                            .defaultSuccessUrl("/dashboard", true)
                            .failureUrl("/login?error")
                            .permitAll()
                    )
                    .logout(logout -> logout
                            .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                            .logoutSuccessUrl("/login?logout")
                            .permitAll()
                    )
                    .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                            .maximumSessions(1));

            return http.build();
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


}
