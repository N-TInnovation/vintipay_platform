package cv.ntinnovation.gateway_payment.application.port.out;

import cv.ntinnovation.gateway_payment.application.core.domain.entity.Usuario;
import org.springframework.security.core.userdetails.UserDetails;

public interface SalvaUsuario {
    void salvar(Usuario usuario);
}
