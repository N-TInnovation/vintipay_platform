package cv.ntinnovation.vintipay.adapter.out.persistence.adapter;

import cv.ntinnovation.vintipay.adapter.out.persistence.entity.TenantEntity;
import cv.ntinnovation.vintipay.adapter.out.persistence.entity.UsuarioEntity;
import cv.ntinnovation.vintipay.adapter.out.persistence.mapper.UsuarioMapper;
import cv.ntinnovation.vintipay.adapter.out.persistence.repository.TenantRepository;
import cv.ntinnovation.vintipay.adapter.out.persistence.repository.UsuarioRepository;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.port.out.BuscaUsuario;
import cv.ntinnovation.vintipay.application.port.out.SalvaUsuarioPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Primary
@Component
@RequiredArgsConstructor
public class UsuarioAdapter implements SalvaUsuarioPort, BuscaUsuario {
    private final UsuarioRepository repository;
    private final TenantRepository tenantRepository;
    private final UsuarioMapper ususarioMapper;

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
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorId(Long id) {
        return repository.findById(id).map(ususarioMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorEmail(String email) {
        return repository.findByEmail(email).map(ususarioMapper::toDomain);
    }
    
    public void adicionarTenantAoUsuario(Long usuarioId, Long tenantId) {
        UsuarioEntity usuario = repository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        TenantEntity tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant não encontrado"));

        usuario.getTenants().add(tenant);
        repository.save(usuario);
    }
}
