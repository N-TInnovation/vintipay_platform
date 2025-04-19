package cv.ntinnovation.gateway_payment.application.core.domain.entity;

import cv.ntinnovation.gateway_payment.application.port.out.DetalhesUsuario;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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


    public static Usuario builder(String username, String password, boolean ativo, String... roles) {
        UserDetails detalhesUsuario = User.withUsername(username).password(password).roles(roles).disabled(!ativo).build();

        return new Usuario(username, password, detalhesUsuario.getAuthorities(), ativo, roles);
    }

    public static DetalhesUsuario builder(String username, String password, String... role) {
        DetalhesUsuario detalhesUsuario = (DetalhesUsuario) User.withUsername(username).password(password).roles(role).build();

        detalhesUsuario.setPassword(password);
        detalhesUsuario.setUsername(username);

        return detalhesUsuario;
    }

    public static DetalhesUsuario builder(String username, String password) {
        DetalhesUsuario detalhesUsuario = (DetalhesUsuario) User.withUsername(username).password(password).build();

        detalhesUsuario.setPassword(password);
        detalhesUsuario.setUsername(username);

        return detalhesUsuario;
    }

    public Usuario(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities){
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }

    public Usuario(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public Usuario(String username, String password, Collection<? extends GrantedAuthority> authorities, boolean ativo, String... role) {
        super(username, password, authorities);
        this.ativo = ativo;
        this.role = role;
    }


    public Usuario(Long id, String username, String password, boolean ativo, String... role) {
        super(username, password, List.of());
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.ativo = ativo;
    }

    @Override
    public String getUsername() {
        return super.getUsername();
    }

    @Override
    public String getPassword() {
        return super.getPassword();
    }

    @Override
    public boolean isAccountNonExpired() {
        return super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return this.ativo;
    }

}
