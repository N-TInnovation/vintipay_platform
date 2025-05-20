package cv.ntinnovation.vintipay.adapter.out.db.adapter;

import cv.ntinnovation.vintipay.adapter.out.db.entity.UsuarioEntity;
import cv.ntinnovation.vintipay.adapter.out.db.mapper.UsusarioMapper;
import cv.ntinnovation.vintipay.adapter.out.db.repository.UsuarioRepository;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.port.out.BuscaUsuario;
import cv.ntinnovation.vintipay.application.port.out.SalvaUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Primary
@Component
@RequiredArgsConstructor
public class UsuarioAdapter implements SalvaUsuario, BuscaUsuario {
    private final UsuarioRepository repository;
    private final UsusarioMapper ususarioMapper;

    @Override
    public Usuario salvar(Usuario usuario) {
        var user = repository.save(ususarioMapper.toEntity(usuario));
        return ususarioMapper.toDomain(user);
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
        
        // Retornar o próprio objeto Usuario, que já implementa UserDetails
        // e agora tem um método getAuthorities() atualizado para usar papéis e permissões
        return usuario;
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return repository.findById(id).map(ususarioMapper::toDomain);
    }
}
