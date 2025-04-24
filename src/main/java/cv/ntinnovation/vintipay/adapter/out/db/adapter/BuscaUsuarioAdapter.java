package cv.ntinnovation.vintipay.adapter.out.db.adapter;

import cv.ntinnovation.vintipay.adapter.out.db.mapper.UsusarioMapper;
import cv.ntinnovation.vintipay.adapter.out.db.repository.UsuarioRepository;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.port.out.BuscaUsuarioPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class BuscaUsuarioAdapter implements BuscaUsuarioPort {
    private final UsuarioRepository usuarioRepository;
    private final UsusarioMapper ususarioMapper;

    @Override
    public Optional<Usuario> getUser(String username) {
        var usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
        return Optional.of(ususarioMapper.toDomain(usuario));
    }
    
    @Override
    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .map(ususarioMapper::toDomain);
    }
}
