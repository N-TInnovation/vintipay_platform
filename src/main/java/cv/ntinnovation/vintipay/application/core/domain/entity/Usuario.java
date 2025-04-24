package cv.ntinnovation.vintipay.application.core.domain.entity;

import cv.ntinnovation.vintipay.application.port.out.DetalhesUsuario;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class Usuario extends User {
    @Getter
    @Setter
    private Long id;
    @Getter
    @Setter
    private String[] role;
    @Getter
    @Setter
    private boolean ativo;
    @Setter
    private String username;
    @Setter
    private String password;
    
    @Getter
    @Setter
    private String email;
    
    @Getter
    @Setter
    private String nome;
    
    @Getter
    @Setter
    private String telefone;
    
    @Getter
    @Setter
    private String stripeCustomerId;
    
    @Getter
    @Setter
    private LocalDateTime dataRegistro;
    
    @Getter
    @Setter
    private LocalDateTime ultimoLogin;
    
    @Getter
    @Setter
    private PlanoAssinatura planoAssinatura;

    /**
     * Construtor padrão para criar instâncias simplificadas
     * Usado principalmente para referências em relacionamentos
     */
    public Usuario() {
        super("placeholder", "placeholder", Collections.singletonList(new SimpleGrantedAuthority("ROLE_PLACEHOLDER")));
        this.username = null;
        this.password = null;
    }

    /**
     * Construtor principal para criar instâncias completas
     */
    private Usuario(String username, String password, Collection<? extends GrantedAuthority> authorities, boolean ativo, String[] roles) {
        super(username, password, authorities);
        this.username = username;
        this.password = password;
        this.ativo = ativo;
        this.role = roles;
    }

    /**
     * Cria uma instância de Usuario com informações básicas
     */
    public static Usuario builder(String username, String password, boolean ativo, String... roles) {
        if (username == null) {
            throw new IllegalArgumentException("Username cannot be null");
        }
        
        UserDetails detalhesUsuario = User.withUsername(username)
                .password(password != null ? password : "")
                .roles(roles != null ? roles : new String[]{"USER"})
                .disabled(!ativo)
                .build();

        return new Usuario(username, password, detalhesUsuario.getAuthorities(), ativo, roles);
    }
    
    /**
     * Cria uma instância de Usuario com informações adicionais
     */
    public static Usuario builder(String username, String email, String nome, String password, boolean ativo, String... roles) {
        if (username == null) {
            throw new IllegalArgumentException("Username cannot be null");
        }
        
        Usuario usuario = builder(username, password, ativo, roles);
        usuario.setEmail(email);
        usuario.setNome(nome);
        usuario.setDataRegistro(LocalDateTime.now());
        
        return usuario;
    }
    
    /**
     * Cria uma instância simplificada de Usuario apenas com o ID
     * Útil para referências em relacionamentos
     */
    public static Usuario referenceById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        
        Usuario usuario = new Usuario();
        usuario.setId(id);
        return usuario;
    }

    /**
     * Método de compatibilidade para código legado
     * @deprecated Use {@link #builder(String, String, boolean, String...)} instead
     */
    @Deprecated
    public static Usuario builder(String username, String password, String... role) {
        try {
            return builder(username, password, true, role);
        } catch (Exception e) {
            // Fallback para manter compatibilidade
            Usuario usuario = new Usuario();
            usuario.setUsername(username);
            usuario.setPassword(password);
            usuario.setRole(role);
            usuario.setAtivo(true);
            return usuario;
        }
    }

    /**
     * Método de compatibilidade para código legado
     * @deprecated Use {@link #builder(String, String, boolean, String...)} instead
     */
    @Deprecated
    public static Usuario builder(String username, String password) {
        return builder(username, password, true, "USER");
    }

    /**
     * Construtor de compatibilidade
     */
    public Usuario(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.username = username;
        this.password = password;
        this.ativo = enabled;
    }

    /**
     * Construtor de compatibilidade
     */
    public Usuario(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.username = username;
        this.password = password;
        this.ativo = true;
    }

    @Override
    public String getUsername() {
        return username;
    }
    
    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.ativo;
    }
}
