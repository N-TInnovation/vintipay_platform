package cv.ntinnovation.vintipay.adapter.out.persistence.adapter;

import cv.ntinnovation.vintipay.adapter.out.persistence.mapper.UsuarioMapper;
import cv.ntinnovation.vintipay.adapter.out.persistence.repository.UsuarioRepository;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.port.out.BuscaUsuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class BuscaUsuarioImpl implements BuscaUsuario {
    
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    @Override
    public String getusername(String username) {
        return username;
    }

    @Override
    public UserDetails getUser(String username) {
        log.debug("Buscando usuário com username: {}", username);
        Usuario usuario = usuarioRepository.findByUsername(username)
                .map(usuarioMapper::toDomain)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
        
        return User.builder()
                .username(usuario.getUsername())
                .password(usuario.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + usuario.getRole())))
                .disabled(!usuario.isAtivo())
                .accountExpired(false)
                .credentialsExpired(false)
                .accountLocked(false)
                .build();
    }
    
    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        log.debug("Buscando usuário com ID: {}", id);
        return usuarioRepository.findById(id)
                .map(usuarioMapper::toDomain);
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        log.debug("Buscando usuário com email: {}", email);
        return usuarioRepository.findByEmail(email)
                .map(usuarioMapper::toDomain);
    }
}
