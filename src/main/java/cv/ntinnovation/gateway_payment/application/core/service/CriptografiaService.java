package cv.ntinnovation.gateway_payment.application.core.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CriptografiaService {
    private final BCryptPasswordEncoder passwordEncoder;

    public CriptografiaService() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public String criptografar(String texto) {
        return passwordEncoder.encode(texto);
    }

    public boolean verificar(String texto, String textoCriptografado) {
        return passwordEncoder.matches(texto, textoCriptografado);
    }
}
