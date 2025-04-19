package cv.ntinnovation.gateway_payment.application.port.out;

import cv.ntinnovation.gateway_payment.application.core.domain.entity.Usuario;

public interface BuscaUsuario {
    public String getusername(String username);
    public Usuario getUser(String username);
}
