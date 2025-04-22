package cv.ntinnovation.vintipay.application.port.out;

import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;

import java.util.Optional;

public interface BuscaUsuarioPort {
    Optional<Usuario> getUser(String username);
}
