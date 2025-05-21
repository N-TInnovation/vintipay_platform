package cv.ntinnovation.vintipay.application.port.out;

import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;

public interface SalvaUsuarioPort {
    Usuario salvar(Usuario usuario);
}
