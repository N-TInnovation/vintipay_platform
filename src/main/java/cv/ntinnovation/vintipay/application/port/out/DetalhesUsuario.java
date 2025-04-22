package cv.ntinnovation.vintipay.application.port.out;

import org.springframework.security.core.userdetails.UserDetails;

public interface DetalhesUsuario extends UserDetails {

    void setPassword(String password);
    void setUsername(String username);
    void setRole(String role);
    void setAtivo(boolean ativo);
}
