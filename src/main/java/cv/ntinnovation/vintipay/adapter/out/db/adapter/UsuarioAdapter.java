package cv.ntinnovation.vintipay.adapter.out.db.adapter;

import cv.ntinnovation.vintipay.adapter.out.db.entity.UsuarioEntity;
import cv.ntinnovation.vintipay.adapter.out.db.mapper.UsusarioMapper;
import cv.ntinnovation.vintipay.adapter.out.db.repository.UsuarioRepository;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.port.out.BuscaUsuario;
import cv.ntinnovation.vintipay.application.port.out.SalvaUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Primary
@Component
@RequiredArgsConstructor
public class UsuarioAdapter implements SalvaUsuario, BuscaUsuario {
    private final UsuarioRepository repository;
    private final UsusarioMapper ususarioMapper;

    @Override
    public void salvar(Usuario usuario) {
        repository.save(ususarioMapper.toEntity(usuario));
    }

    @Override
    public String getusername(String username) {
        return repository.findByUsername(username).map(UsuarioEntity::getUsername).orElse("");
    }

    @Override
    public UserDetails getUser(String username) {
        Usuario usuario = repository.findByUsername(username)
                .map(ususarioMapper::toDomain)
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
}
