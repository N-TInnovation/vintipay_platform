package cv.ntinnovation.vintipay.application.core.domain.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para a entidade de preferências de notificação
 */
public class PreferenciasNotificacaoTest {

    @Test
    public void testGetDefault() {
        // Given
        Long usuarioId = 123L;
        
        // When
        PreferenciasNotificacao preferencias = PreferenciasNotificacao.getDefault(usuarioId);
        
        // Then
        assertNotNull(preferencias);
        assertEquals(usuarioId, preferencias.getUsuarioId());
        assertTrue(preferencias.isEmail());
        assertTrue(preferencias.isSms());
        assertFalse(preferencias.isPush());
        assertTrue(preferencias.isTransacoes());
        assertFalse(preferencias.isPromocoes());
        assertTrue(preferencias.isSeguranca());
    }
    
    @Test
    public void testBuilder() {
        // Given
        Long id = 1L;
        Long usuarioId = 123L;
        
        // When
        PreferenciasNotificacao preferencias = PreferenciasNotificacao.builder()
                .id(id)
                .usuarioId(usuarioId)
                .email(false)
                .sms(true)
                .push(true)
                .transacoes(false)
                .promocoes(true)
                .seguranca(false)
                .build();
        
        // Then
        assertNotNull(preferencias);
        assertEquals(id, preferencias.getId());
        assertEquals(usuarioId, preferencias.getUsuarioId());
        assertFalse(preferencias.isEmail());
        assertTrue(preferencias.isSms());
        assertTrue(preferencias.isPush());
        assertFalse(preferencias.isTransacoes());
        assertTrue(preferencias.isPromocoes());
        assertFalse(preferencias.isSeguranca());
    }
}
