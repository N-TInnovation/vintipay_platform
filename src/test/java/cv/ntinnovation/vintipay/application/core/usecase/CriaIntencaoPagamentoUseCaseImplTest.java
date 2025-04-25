package cv.ntinnovation.vintipay.application.core.usecase;

import cv.ntinnovation.vintipay.application.core.domain.entity.ContaPagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.IntencaoPagamento;
import cv.ntinnovation.vintipay.application.core.domain.enums.StatusIntencaoPagamentoEnum;
import cv.ntinnovation.vintipay.application.core.domain.enums.StatusPagamentoEnum;
import cv.ntinnovation.vintipay.application.port.out.SalvaIntencaoPagamento;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CriaIntencaoPagamentoUseCaseImplTest {

    @Mock
    private SalvaIntencaoPagamento salvaIntencaoPagamento;

    @InjectMocks
    private CriaIntencaoPagamentoUseCaseImpl criaIntencaoPagamentoUseCase;

    @Test
    void criar_QuandoIntencaoPagamentoValida_DeveRetornarIntencaoPagamentoSalva() {
        // Arrange
        IntencaoPagamento intencaoPagamento = IntencaoPagamento.builder()
                .valor(BigDecimal.valueOf(100.50))
                .contaPagamento(ContaPagamento.builder().id(1L).build())
                .moeda("CVE")
                .build();

        IntencaoPagamento intencaoPagamentoSalva = IntencaoPagamento.builder()
                .id(1L)
                .valor(BigDecimal.valueOf(100.50))
                .contaPagamento(ContaPagamento.builder().id(1L).build())
                .status(StatusIntencaoPagamentoEnum.PENDENTE)
                .referencia("REF123")
                .moeda("CVE")
                .build();

        when(salvaIntencaoPagamento.salvar(any(IntencaoPagamento.class))).thenReturn(intencaoPagamentoSalva);

        // Act
        IntencaoPagamento resultado = criaIntencaoPagamentoUseCase.criar(intencaoPagamento);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(BigDecimal.valueOf(100.50), resultado.getValor());
        assertEquals(StatusPagamentoEnum.PENDENTE, resultado.getStatus());
        assertNotNull(resultado.getReferencia());
        
        verify(salvaIntencaoPagamento, times(1)).salvar(any(IntencaoPagamento.class));
    }
    
    @Test
    void criar_QuandoIntencaoPagamentoValidaComEmail_DeveRetornarIntencaoPagamentoSalva() {
        // Arrange
        IntencaoPagamento intencaoPagamento = IntencaoPagamento.builder()
                .valor(BigDecimal.valueOf(100.50))
                .contaPagamentoEmail("cliente@example.com")
                .contaPagamentoBillAddrCity("Praia")
                .contaPagamentoBillAddrCountry("CV")
                .contaPagamentoBillAddrLine1("Rua Principal, 123")
                .contaPagamentoBillAddrPostCode("12345")
                .moeda("CVE")
                .build();

        IntencaoPagamento intencaoPagamentoSalva = IntencaoPagamento.builder()
                .id(1L)
                .valor(BigDecimal.valueOf(100.50))
                .contaPagamentoEmail("cliente@example.com")
                .contaPagamentoBillAddrCity("Praia")
                .contaPagamentoBillAddrCountry("CV")
                .contaPagamentoBillAddrLine1("Rua Principal, 123")
                .contaPagamentoBillAddrPostCode("12345")
                .status(StatusIntencaoPagamentoEnum.PENDENTE.PENDENTE)
                .referencia("REF123")
                .moeda("CVE")
                .build();

        when(salvaIntencaoPagamento.salvar(any(IntencaoPagamento.class))).thenReturn(intencaoPagamentoSalva);

        // Act
        IntencaoPagamento resultado = criaIntencaoPagamentoUseCase.criar(intencaoPagamento);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(BigDecimal.valueOf(100.50), resultado.getValor());
        assertEquals(StatusPagamentoEnum.PENDENTE, resultado.getStatus());
        assertNotNull(resultado.getReferencia());
        assertEquals("cliente@example.com", resultado.getContaPagamentoEmail());
        
        verify(salvaIntencaoPagamento, times(1)).salvar(any(IntencaoPagamento.class));
    }

    @Test
    void criar_QuandoAmountNulo_DeveLancarExcecao() {
        // Arrange
        IntencaoPagamento intencaoPagamento = IntencaoPagamento.builder()
                .contaPagamento(ContaPagamento.builder().id(1L).build())
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> criaIntencaoPagamentoUseCase.criar(intencaoPagamento));
        
        assertEquals("O valor do pagamento é obrigatório", exception.getMessage());
        verify(salvaIntencaoPagamento, never()).salvar(any(IntencaoPagamento.class));
    }

    @Test
    void criar_QuandoAmountNegativo_DeveLancarExcecao() {
        // Arrange
        IntencaoPagamento intencaoPagamento = IntencaoPagamento.builder()
                .valor(BigDecimal.valueOf(-10.0))
                .contaPagamento(ContaPagamento.builder().id(1L).build())
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> criaIntencaoPagamentoUseCase.criar(intencaoPagamento));
        
        assertEquals("O valor do pagamento deve ser maior que zero", exception.getMessage());
        verify(salvaIntencaoPagamento, never()).salvar(any(IntencaoPagamento.class));
    }

    @Test
    void criar_QuandoMetodoPagamentoNulo_DeveLancarExcecao() {
        // Arrange
        IntencaoPagamento intencaoPagamento = IntencaoPagamento.builder()
                .valor(BigDecimal.valueOf(100.50))
                .contaPagamento(ContaPagamento.builder().id(1L).build())
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> criaIntencaoPagamentoUseCase.criar(intencaoPagamento));
        
        assertEquals("O método de pagamento é obrigatório", exception.getMessage());
        verify(salvaIntencaoPagamento, never()).salvar(any(IntencaoPagamento.class));
    }

    @Test
    void criar_QuandoContaPagamentoEEmailNulos_DeveLancarExcecao() {
        // Arrange
        IntencaoPagamento intencaoPagamento = IntencaoPagamento.builder()
                .valor(BigDecimal.valueOf(100.50))
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> criaIntencaoPagamentoUseCase.criar(intencaoPagamento));
        
        assertEquals("A conta de pagamento ou informações para criação são obrigatórias", exception.getMessage());
        verify(salvaIntencaoPagamento, never()).salvar(any(IntencaoPagamento.class));
    }
    
    @Test
    void criar_QuandoContaPagamentoNulaEEmailVazio_DeveLancarExcecao() {
        // Arrange
        IntencaoPagamento intencaoPagamento = IntencaoPagamento.builder()
                .valor(BigDecimal.valueOf(100.50))
                .contaPagamentoEmail("")
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> criaIntencaoPagamentoUseCase.criar(intencaoPagamento));
        
        assertEquals("A conta de pagamento ou informações para criação são obrigatórias", exception.getMessage());
        verify(salvaIntencaoPagamento, never()).salvar(any(IntencaoPagamento.class));
    }
}
