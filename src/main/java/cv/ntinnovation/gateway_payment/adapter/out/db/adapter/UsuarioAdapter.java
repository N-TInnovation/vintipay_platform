package cv.ntinnovation.gateway_payment.adapter.out.db.adapter;

import cv.ntinnovation.gateway_payment.adapter.out.db.entity.UsuarioEntity;
import cv.ntinnovation.gateway_payment.adapter.out.db.mapper.UsusarioMapper;
import cv.ntinnovation.gateway_payment.adapter.out.db.repository.UsuarioRepository;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.Usuario;
import cv.ntinnovation.gateway_payment.application.port.out.BuscaUsuario;
import cv.ntinnovation.gateway_payment.application.port.out.SalvaUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

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
    public Usuario getUser(String username) {
       return repository.findByUsername(username).map(ususarioMapper::toDomain).orElse(null);

    }

}
