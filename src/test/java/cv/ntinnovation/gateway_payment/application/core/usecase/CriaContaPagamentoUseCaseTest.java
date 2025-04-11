package cv.ntinnovation.gateway_payment.application.core.usecase;

import cv.ntinnovation.gateway_payment.application.core.domain.entity.ContaPagamento;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.InfoContaPagamento;
import cv.ntinnovation.gateway_payment.application.port.out.BuscaContaPagamentoPorEmail;
import cv.ntinnovation.gateway_payment.application.port.out.SalvaContaPagamento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CriaContaPagamentoUseCaseTest {

    @Mock
    private BuscaContaPagamentoPorEmail buscaContaPagamentoPorEmail;

    @Mock
    private SalvaContaPagamento salvaContaPagamento;

    @InjectMocks
    private CriaContaPagamentoUseCaseImpl criaContaPagamentoUseCase;

    private String email;
    private String billAddrCity;
    private String billAddrCountry;
    private String billAddrLine1;
    private String billAddrPostCode;

    @BeforeEach
    void setUp() {
        email = "cliente@example.com";
        billAddrCity = "Praia";
        billAddrCountry = "CV";
        billAddrLine1 = "Rua Principal, 123";
        billAddrPostCode = "12345";
    }

    @Test
    void criarOuBuscarPorEmail_QuandoContaNaoExiste_DeveCriarNovaConta() {
        // Arrange
        when(buscaContaPagamentoPorEmail.buscarPorEmail(anyString())).thenReturn(null);
        when(salvaContaPagamento.salvar(any(ContaPagamento.class))).thenAnswer(invocation -> {
            ContaPagamento contaSalva = invocation.getArgument(0);
            contaSalva.setId(1L);
            return contaSalva;
        });

        // Act
        ContaPagamento resultado = criaContaPagamentoUseCase.criarOuBuscarPorEmail(
                email, billAddrCity, billAddrCountry, billAddrLine1, billAddrPostCode);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(email, resultado.getEmail());
        assertEquals(billAddrCity, resultado.getBillAddrCity());
        assertEquals(billAddrCountry, resultado.getBillAddrCountry());
        assertEquals(billAddrLine1, resultado.getBillAddrLine1());
        assertEquals(billAddrPostCode, resultado.getBillAddrPostCode());
        assertNotNull(resultado.getAcctInfo());
        
        // Verify
        verify(buscaContaPagamentoPorEmail).buscarPorEmail(email);
        verify(salvaContaPagamento).salvar(any(ContaPagamento.class));
    }

    @Test
    void criarOuBuscarPorEmail_QuandoContaExiste_DeveRetornarContaExistente() {
        // Arrange
        ContaPagamento contaExistente = createSampleContaPagamento();
        when(buscaContaPagamentoPorEmail.buscarPorEmail(email)).thenReturn(contaExistente);

        // Act
        ContaPagamento resultado = criaContaPagamentoUseCase.criarOuBuscarPorEmail(
                email, billAddrCity, billAddrCountry, billAddrLine1, billAddrPostCode);

        // Assert
        assertNotNull(resultado);
        assertEquals(contaExistente.getId(), resultado.getId());
        assertEquals(contaExistente.getEmail(), resultado.getEmail());
        
        // Verify
        verify(buscaContaPagamentoPorEmail).buscarPorEmail(email);
        verify(salvaContaPagamento, never()).salvar(any(ContaPagamento.class));
    }

    @Test
    void criarOuBuscarPorEmail_QuandoContaExisteMasEnderecoMudou_DeveAtualizarEndereco() {
        // Arrange
        ContaPagamento contaExistente = createSampleContaPagamento();
        contaExistente.setBillAddrCity("Cidade Antiga");
        contaExistente.setBillAddrCountry("País Antigo");
        
        when(buscaContaPagamentoPorEmail.buscarPorEmail(email)).thenReturn(contaExistente);
        when(salvaContaPagamento.salvar(any(ContaPagamento.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ContaPagamento resultado = criaContaPagamentoUseCase.criarOuBuscarPorEmail(
                email, billAddrCity, billAddrCountry, billAddrLine1, billAddrPostCode);

        // Assert
        assertNotNull(resultado);
        assertEquals(contaExistente.getId(), resultado.getId());
        assertEquals(email, resultado.getEmail());
        assertEquals(billAddrCity, resultado.getBillAddrCity());
        assertEquals(billAddrCountry, resultado.getBillAddrCountry());
        
        // Verify
        verify(buscaContaPagamentoPorEmail).buscarPorEmail(email);
        verify(salvaContaPagamento).salvar(any(ContaPagamento.class));
    }

    @Test
    void criarOuBuscarPorEmail_QuandoEmailNulo_DeveLancarExcecao() {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            criaContaPagamentoUseCase.criarOuBuscarPorEmail(
                    null, billAddrCity, billAddrCountry, billAddrLine1, billAddrPostCode);
        });
        
        assertTrue(exception.getMessage().contains("Email não pode ser nulo"));
        
        // Verify
        verify(buscaContaPagamentoPorEmail, never()).buscarPorEmail(anyString());
        verify(salvaContaPagamento, never()).salvar(any(ContaPagamento.class));
    }

    private ContaPagamento createSampleContaPagamento() {
        LocalDateTime agora = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String dataFormatada = agora.format(formatter);
        
        InfoContaPagamento acctInfo = InfoContaPagamento.builder()
                .acctID("ACC123456789")
                .chAccAgeInd("01")
                .chAccDate(dataFormatada)
                .chAccChange(dataFormatada)
                .chAccPwChange(dataFormatada)
                .chAccPwChangeInd("01")
                .suspiciousAccActivity("01")
                .build();
                
        return ContaPagamento.builder()
                .id(1L)
                .email(email)
                .billAddrCity(billAddrCity)
                .billAddrCountry(billAddrCountry)
                .billAddrLine1(billAddrLine1)
                .billAddrPostCode(billAddrPostCode)
                .acctInfo(acctInfo)
                .build();
    }
}
