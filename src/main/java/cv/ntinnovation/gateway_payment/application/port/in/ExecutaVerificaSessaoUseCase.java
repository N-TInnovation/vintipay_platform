package cv.ntinnovation.gateway_payment.application.port.in;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface ExecutaVerificaSessaoUseCase {
    public void verificar(String session);
}
