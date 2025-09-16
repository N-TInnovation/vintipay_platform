package cv.ntinnovation.vintipay.adapter.out.persistence.adapter;

import cv.ntinnovation.vintipay.adapter.out.persistence.mapper.UsuarioMapper;
import cv.ntinnovation.vintipay.adapter.out.persistence.repository.UsuarioRepository;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.port.out.BuscaUsuarioPorIdPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class BuscaUsuarioPorIdAdapter implements BuscaUsuarioPorIdPort {
    
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    
    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        log.debug("Buscando usuário com ID: {}", id);
        return usuarioRepository.findById(id)
                .map(usuarioMapper::toDomain);
    }
}
