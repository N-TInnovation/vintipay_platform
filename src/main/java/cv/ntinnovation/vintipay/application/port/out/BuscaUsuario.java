package cv.ntinnovation.vintipay.application.port.out;

import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;

public interface BuscaUsuario {
    public String getusername(String username);
    public Usuario getUser(String username);
}
