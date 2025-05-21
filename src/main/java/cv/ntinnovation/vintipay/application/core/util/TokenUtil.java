package cv.ntinnovation.vintipay.application.core.util;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

/**
 * Classe utilitária para geração e validação de tokens
 */
public class TokenUtil {
    
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();
    
    /**
     * Gera um token seguro para verificação de email
     * @return Token seguro
     */
    public static String generateToken() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }
    
    /**
     * Gera um código numérico de 6 dígitos para verificação
     * @return Código de 6 dígitos
     */
    public static String generateNumericCode() {
        int code = 100000 + secureRandom.nextInt(900000);
        return String.valueOf(code);
    }
    
    /**
     * Verifica se um token expirou
     * @param expirationDate Data de expiração
     * @return true se expirou, false caso contrário
     */
    public static boolean isTokenExpired(LocalDateTime expirationDate) {
        if (expirationDate == null) {
            return true;
        }
        return LocalDateTime.now().isAfter(expirationDate);
    }
    
    /**
     * Calcula a data de expiração para um token
     * @param hours Horas até a expiração
     * @return Data de expiração
     */
    public static LocalDateTime calculateExpiryDate(int hours) {
        return LocalDateTime.now().plusHours(hours);
    }
}
