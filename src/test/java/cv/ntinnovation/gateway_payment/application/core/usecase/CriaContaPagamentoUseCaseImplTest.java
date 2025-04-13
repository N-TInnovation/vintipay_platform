package cv.ntinnovation.gateway_payment.application.core.usecase;

import cv.ntinnovation.gateway_payment.application.core.domain.entity.ContaPagamento;
import cv.ntinnovation.gateway_payment.application.port.out.BuscaContaPagamentoPorEmail;
import cv.ntinnovation.gateway_payment.application.port.out.SalvaContaPagamento;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CriaContaPagamentoUseCaseImplTest {

    @Mock
    private BuscaContaPagamentoPorEmail buscaContaPagamentoPorEmail;

    @Mock
    private SalvaContaPagamento salvaContaPagamento;

    @InjectMocks
    private CriaContaPagamentoUseCaseImpl criaContaPagamentoUseCase;

    @Test
    void criarOuBuscarPorEmail_QuandoContaExiste_DeveRetornarContaExistente() {
        // Arrange
        String email = "cliente@example.com";
        String cidade = "Praia";
        String pais = "CV";
        String endereco = "Rua Principal, 123";
        String cep = "12345";
        String phoneCode = "55";
        String phoneNumber = "1234567890";
        
        ContaPagamento contaExistente = ContaPagamento.builder()
                .id(1L)
                .email(email)
                .billAddrCity(cidade)
                .billAddrCountry(pais)
                .billAddrLine1(endereco)
                .billAddrPostCode(cep)
                .build();
        
        when(buscaContaPagamentoPorEmail.buscarPorEmail(email)).thenReturn(contaExistente);
        
        // Act
        ContaPagamento resultado = criaContaPagamentoUseCase.criarOuBuscarPorEmail(email, cidade, pais, endereco, cep, phoneCode, phoneNumber);
        
        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(email, resultado.getEmail());
        assertEquals(cidade, resultado.getBillAddrCity());
        
        verify(buscaContaPagamentoPorEmail, times(1)).buscarPorEmail(email);
        verify(salvaContaPagamento, never()).salvar(any(ContaPagamento.class));
    }
    
    @Test
    void criarOuBuscarPorEmail_QuandoContaNaoExiste_DeveCriarNovaConta() {
        // Arrange
        String email = "novocliente@example.com";
        String cidade = "Mindelo";
        String pais = "CV";
        String endereco = "Avenida Principal, 456";
        String cep = "54321";
        String phoneCode = "55";
        String phoneNumber = "1234567890";
        
        ContaPagamento novaConta = ContaPagamento.builder()
                .email(email)
                .billAddrCity(cidade)
                .billAddrCountry(pais)
                .billAddrLine1(endereco)
                .billAddrPostCode(cep)
                .build();
        
        ContaPagamento contaSalva = ContaPagamento.builder()
                .id(2L)
                .email(email)
                .billAddrCity(cidade)
                .billAddrCountry(pais)
                .billAddrLine1(endereco)
                .billAddrPostCode(cep)
                .build();
        
        when(buscaContaPagamentoPorEmail.buscarPorEmail(email)).thenReturn(null);
        when(salvaContaPagamento.salvar(any(ContaPagamento.class))).thenReturn(contaSalva);
        
        // Act
        ContaPagamento resultado = criaContaPagamentoUseCase.criarOuBuscarPorEmail(email, cidade, pais, endereco, cep, phoneCode, phoneNumber);
        
        // Assert
        assertNotNull(resultado);
        assertEquals(2L, resultado.getId());
        assertEquals(email, resultado.getEmail());
        assertEquals(cidade, resultado.getBillAddrCity());
        
        verify(buscaContaPagamentoPorEmail, times(1)).buscarPorEmail(email);
        verify(salvaContaPagamento, times(1)).salvar(any(ContaPagamento.class));
    }
    
    @Test
    void criarOuBuscarPorEmail_QuandoEmailNulo_DeveLancarExcecao() {
        // Arrange
        String email = null;
        String cidade = "Praia";
        String pais = "CV";
        String endereco = "Rua Principal, 123";
        String cep = "12345";
        String phoneCode = "55";
        String phoneNumber = "1234567890";
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> criaContaPagamentoUseCase.criarOuBuscarPorEmail(email, cidade, pais, endereco, cep, phoneCode, phoneNumber));
        
        assertEquals("Email é obrigatório para criar ou buscar uma conta de pagamento", exception.getMessage());
        
        verify(buscaContaPagamentoPorEmail, never()).buscarPorEmail(anyString());
        verify(salvaContaPagamento, never()).salvar(any(ContaPagamento.class));
    }
    
    @Test
    void criarOuBuscarPorEmail_QuandoEmailVazio_DeveLancarExcecao() {
        // Arrange
        String email = "";
        String cidade = "Praia";
        String pais = "CV";
        String endereco = "Rua Principal, 123";
        String cep = "12345";
        String phoneCode = "55";
        String phoneNumber = "1234567890";
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> criaContaPagamentoUseCase.criarOuBuscarPorEmail(email, cidade, pais, endereco, cep, phoneCode, phoneNumber));
        
        assertEquals("Email é obrigatório para criar ou buscar uma conta de pagamento", exception.getMessage());
        
        verify(buscaContaPagamentoPorEmail, never()).buscarPorEmail(anyString());
        verify(salvaContaPagamento, never()).salvar(any(ContaPagamento.class));
    }
}
