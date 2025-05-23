package cv.ntinnovation.vintipay.application.core.usecase;

import cv.ntinnovation.vintipay.application.core.domain.entity.PreferenciasNotificacao;
import cv.ntinnovation.vintipay.application.port.out.GerenciaPreferenciasNotificacaoPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testes para a implementação do caso de uso de gerenciamento de preferências de notificação
 */
public class GerenciaPreferenciasNotificacaoUseCaseImplTest {

    @Mock
    private GerenciaPreferenciasNotificacaoPort gerenciaPreferenciasNotificacaoPort;

    @InjectMocks
    private GerenciaPreferenciasNotificacaoUseCaseImpl gerenciaPreferenciasNotificacaoUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testBuscarPreferenciasDoUsuario() {
        // Given
        Long usuarioId = 1L;
        PreferenciasNotificacao preferenciasEsperadas = PreferenciasNotificacao.getDefault(usuarioId);
        when(gerenciaPreferenciasNotificacaoPort.buscarPorUsuario(usuarioId)).thenReturn(preferenciasEsperadas);

        // When
        PreferenciasNotificacao resultado = gerenciaPreferenciasNotificacaoUseCase.buscarPreferenciasDoUsuario(usuarioId);

        // Then
        assertNotNull(resultado);
        assertEquals(preferenciasEsperadas, resultado);
        verify(gerenciaPreferenciasNotificacaoPort, times(1)).buscarPorUsuario(usuarioId);
    }

    @Test
    void testAtualizarPreferencias() {
        // Given
        Long usuarioId = 1L;
        PreferenciasNotificacao preferenciasAtualizadas = PreferenciasNotificacao.builder()
                .id(1L)
                .usuarioId(usuarioId)
                .email(true)
                .sms(false)
                .push(true)
                .transacoes(true)
                .promocoes(true)
                .seguranca(true)
                .build();
        
        when(gerenciaPreferenciasNotificacaoPort.salvar(any(PreferenciasNotificacao.class)))
                .thenReturn(preferenciasAtualizadas);

        // When
        PreferenciasNotificacao resultado = gerenciaPreferenciasNotificacaoUseCase.atualizarPreferencias(preferenciasAtualizadas);

        // Then
        assertNotNull(resultado);
        assertEquals(preferenciasAtualizadas, resultado);
        assertEquals(true, resultado.isEmail());
        assertEquals(false, resultado.isSms());
        assertEquals(true, resultado.isPush());
        verify(gerenciaPreferenciasNotificacaoPort, times(1)).salvar(preferenciasAtualizadas);
    }
}
