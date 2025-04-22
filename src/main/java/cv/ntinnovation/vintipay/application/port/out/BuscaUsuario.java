package cv.ntinnovation.vintipay.application.port.out;

import org.springframework.security.core.userdetails.UserDetails;

public interface BuscaUsuario {
    public String getusername(String username);
    public UserDetails getUser(String username);
}
