package cv.ntinnovation.vintipay.adapter.in.web.security;

import cv.ntinnovation.vintipay.adapter.out.persistence.entity.UsuarioEntity;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Collection;
import java.util.Map;

@Getter
public class CustomOAuth2User implements OidcUser {

    private final OidcUser delegate;
    private final Usuario usuario;

    public CustomOAuth2User(OidcUser delegate, Usuario usuario) {
        this.delegate = delegate;
        this.usuario = usuario;
    }

    @Override
    public Map<String, Object> getClaims() {
        return delegate.getClaims();
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return delegate.getUserInfo();
    }

    @Override
    public OidcIdToken getIdToken() {
        return delegate.getIdToken();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return delegate.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return delegate.getAuthorities();
    }

    @Override
    public String getName() {
        return delegate.getName();
    }
}
