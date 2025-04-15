package cv.ntinnovation.gateway_payment.adapter.out.db.adapter;

import cv.ntinnovation.gateway_payment.adapter.out.db.mapper.UsusarioMapper;
import cv.ntinnovation.gateway_payment.adapter.out.db.repository.UsuarioRepository;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.Usuario;
import cv.ntinnovation.gateway_payment.application.port.out.BuscaUsuarioPort;
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
}
