package cv.ntinnovation.gateway_payment.application.port.out;

import cv.ntinnovation.gateway_payment.application.core.domain.entity.Usuario;
import org.springframework.security.core.userdetails.User;

public interface EncontraUsuario {
    public String getusername(String username);
    public Usuario getUser(String username);
}
