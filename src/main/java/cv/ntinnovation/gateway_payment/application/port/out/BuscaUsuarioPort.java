package cv.ntinnovation.gateway_payment.application.port.out;

import cv.ntinnovation.gateway_payment.application.core.domain.entity.Usuario;

import java.util.Optional;

public interface BuscaUsuarioPort {
    Optional<Usuario> getUser(String username);
}
