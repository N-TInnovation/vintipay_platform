package cv.ntinnovation.gateway_payment.application.core.service;

import cv.ntinnovation.gateway_payment.application.core.domain.rules.FingerPrintService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CriptografiaService {
    private final BCryptPasswordEncoder passwordEncoder;
    private final FingerPrintService fingerPrintService;

    /**
     * Criptografa um texto usando BCrypt (ideal para senhas)
     * @param texto Texto a ser criptografado
     * @return Texto criptografado com BCrypt
     */
    public String criptografar(String texto) {
        return passwordEncoder.encode(texto);
    }

    /**
     * Verifica se um texto corresponde a um hash BCrypt
     * @param texto Texto original
     * @param textoCriptografado Hash BCrypt
     * @return true se o texto corresponde ao hash
     */
    public boolean verificar(String texto, String textoCriptografado) {
        return passwordEncoder.matches(texto, textoCriptografado);
    }
    
    /**
     * Criptografa um texto usando AES256 (ideal para dados sensíveis que precisam ser descriptografados)
     * @param texto Texto a ser criptografado
     * @return Texto criptografado com AES256 em formato Base64
     */
    public String criptografarAES256(String texto) {
        return fingerPrintService.encryptAES256(texto);
    }
    
    /**
     * Descriptografa um texto criptografado com AES256
     * @param textoCriptografado Texto criptografado em formato Base64
     * @return Texto original descriptografado
     */
    public String descriptografarAES256(String textoCriptografado) {
        return fingerPrintService.decryptAES256(textoCriptografado);
    }
}
