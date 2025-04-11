package cv.ntinnovation.gateway_payment.application.core.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CriptografiaServiceTest {

    private final CriptografiaService criptografiaService = new CriptografiaService();

    @Test
    void deveVerificarSenhaCorretamente() {
        // Given
        String senha = "secret456";
        
        // When
        String senhaCriptografada = criptografiaService.criptografar(senha);
        
        // Then
        assertTrue(criptografiaService.verificar(senha, senhaCriptografada));
        System.out.println("Hash gerado: " + senhaCriptografada);
    }

    @Test
    void deveGerarHashesUnicosParaMesmaSenha() {
        // Given
        String senha = "secret456";
        
        // When
        String hash1 = criptografiaService.criptografar(senha);
        String hash2 = criptografiaService.criptografar(senha);
        
        // Then
        assertNotEquals(hash1, hash2);
        assertTrue(criptografiaService.verificar(senha, hash1));
        assertTrue(criptografiaService.verificar(senha, hash2));
        
        System.out.println("Hash 1: " + hash1);
        System.out.println("Hash 2: " + hash2);
    }
}
