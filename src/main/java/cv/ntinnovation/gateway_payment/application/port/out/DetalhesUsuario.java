package cv.ntinnovation.gateway_payment.application.port.out;

import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

public interface DetalhesUsuario extends UserDetails {

    void setPassword(String password);
    void setUsername(String username);
    void setRole(String role);
    void setAtivo(boolean ativo);
}
