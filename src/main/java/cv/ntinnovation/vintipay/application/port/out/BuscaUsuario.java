package cv.ntinnovation.vintipay.application.port.out;

import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface BuscaUsuario {
    public String getusername(String username);
    public UserDetails getUser(String username);
    public Optional<Usuario> buscarPorId(Long id);
    public Optional<Usuario> buscarPorEmail(String email);
}
