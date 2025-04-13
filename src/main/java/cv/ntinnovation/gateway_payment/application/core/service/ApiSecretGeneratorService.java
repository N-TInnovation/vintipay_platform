package cv.ntinnovation.gateway_payment.application.core.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

@Service
public class ApiSecretGeneratorService {
    
    private static final int SECRET_LENGTH = 32;
    private final SecureRandom secureRandom = new SecureRandom();
    
    public String generate() {
        byte[] randomBytes = new byte[SECRET_LENGTH];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}
