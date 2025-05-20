package cv.ntinnovation.vintipay.application.core.usecase;

import cv.ntinnovation.vintipay.application.core.domain.entity.ContaPagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.ContatoContaPagamento;
import cv.ntinnovation.vintipay.application.port.out.BuscaContaPagamentoPorEmail;
import cv.ntinnovation.vintipay.application.port.out.SalvaContaPagamento;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    void criarOuBuscarPorEmail_QuandoEmailVazio_DeveLancarExcecao() {
        // Arrange
        String email = "";
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            criaContaPagamentoUseCase.criarOuBuscarPorEmail("Nome", email, "Cidade", "Estado", "País", "Endereço", "Complemento", "CEP", "55", "123456789")
        );
        
        verify(buscaContaPagamentoPorEmail, never()).buscarPorEmail(anyString());
        verify(salvaContaPagamento, never()).salvar(any(ContaPagamento.class));
    }

    @Test
    void criarOuBuscarPorEmail_QuandoContaExisteSemMudancaEndereco_DeveRetornarContaExistente() {
        // Arrange
        String nome = "Cliente Existente";
        String email = "cliente@example.com";
        String cidade = "Praia";
        String estado = "Santiago";
        String pais = "CV";
        String endereco = "Rua Principal, 123";
        String complemento = "Apto 101";
        String cep = "12345";
        String phoneCode = "238";
        String phoneNumber = "1234567890";
        
        ContatoContaPagamento contato = ContatoContaPagamento.builder()
                .cc(phoneCode)
                .subscriber(phoneNumber)
                .build();
        
        ContaPagamento contaExistente = ContaPagamento.builder()
                .id(1L)
                .name(nome)
                .email(email)
                .billAddrCity(cidade)
                .billAddrState(estado)
                .billAddrCountry(pais)
                .billAddrLine1(endereco)
                .billAddrLine2(complemento)
                .billAddrPostCode(cep)
                .mobilePhone(contato)
                .build();
        
        when(buscaContaPagamentoPorEmail.buscarPorEmail(email)).thenReturn(contaExistente);
        
        // Act
        ContaPagamento resultado = criaContaPagamentoUseCase.criarOuBuscarPorEmail(
            nome, email, cidade, estado, pais, endereco, complemento, cep, phoneCode, phoneNumber
        );
        
        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(email, resultado.getEmail());
        assertEquals(cidade, resultado.getBillAddrCity());
        
        verify(buscaContaPagamentoPorEmail, times(1)).buscarPorEmail(email);
        verify(salvaContaPagamento, never()).salvar(any(ContaPagamento.class));
    }
    
    @Test
    void criarOuBuscarPorEmail_QuandoContaExisteComMudancaEndereco_DeveAtualizarConta() {
        // Arrange
        String nome = "Cliente Atualizado";
        String email = "cliente@example.com";
        String cidadeAntiga = "Praia";
        String cidadeNova = "Mindelo";
        String estadoAntigo = "Santiago";
        String estadoNovo = "São Vicente";
        String pais = "CV";
        String endereco = "Rua Principal, 123";
        String complemento = "Apto 101";
        String cep = "12345";
        String phoneCode = "238";
        String phoneNumber = "1234567890";
        
        ContatoContaPagamento contato = ContatoContaPagamento.builder()
                .cc(phoneCode)
                .subscriber(phoneNumber)
                .build();
        
        ContaPagamento contaExistente = ContaPagamento.builder()
                .id(1L)
                .name(nome)
                .email(email)
                .billAddrCity(cidadeAntiga)
                .billAddrState(estadoAntigo)
                .billAddrCountry(pais)
                .billAddrLine1(endereco)
                .billAddrLine2(complemento)
                .billAddrPostCode(cep)
                .mobilePhone(contato)
                .build();
        
        ContaPagamento contaAtualizada = ContaPagamento.builder()
                .id(1L)
                .name(nome)
                .email(email)
                .billAddrCity(cidadeNova)
                .billAddrState(estadoNovo)
                .billAddrCountry(pais)
                .billAddrLine1(endereco)
                .billAddrLine2(complemento)
                .billAddrPostCode(cep)
                .mobilePhone(contato)
                .build();
        
        when(buscaContaPagamentoPorEmail.buscarPorEmail(email)).thenReturn(contaExistente);
        when(salvaContaPagamento.salvar(any(ContaPagamento.class))).thenReturn(contaAtualizada);
        
        // Act
        ContaPagamento resultado = criaContaPagamentoUseCase.criarOuBuscarPorEmail(
            nome, email, cidadeNova, estadoNovo, pais, endereco, complemento, cep, phoneCode, phoneNumber
        );
        
        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(cidadeNova, resultado.getBillAddrCity());
        assertEquals(estadoNovo, resultado.getBillAddrState());
        
        verify(buscaContaPagamentoPorEmail, times(1)).buscarPorEmail(email);
        verify(salvaContaPagamento, times(1)).salvar(any(ContaPagamento.class));
    }
    
    @Test
    void criarOuBuscarPorEmail_QuandoContaNaoExiste_DeveCriarNovaConta() {
        // Arrange
        String nome = "Novo Cliente";
        String email = "novocliente@example.com";
        String cidade = "Mindelo";
        String estado = "São Vicente";
        String pais = "CV";
        String endereco = "Avenida Principal, 456";
        String complemento = "Casa 2";
        String cep = "54321";
        String phoneCode = "238";
        String phoneNumber = "9876543210";
        
        ContaPagamento contaSalva = ContaPagamento.builder()
                .id(2L)
                .name(nome)
                .email(email)
                .billAddrCity(cidade)
                .billAddrState(estado)
                .billAddrCountry(pais)
                .billAddrLine1(endereco)
                .billAddrLine2(complemento)
                .billAddrPostCode(cep)
                .mobilePhone(ContatoContaPagamento.builder()
                        .cc(phoneCode)
                        .subscriber(phoneNumber)
                        .build())
                .build();
        
        when(buscaContaPagamentoPorEmail.buscarPorEmail(email)).thenReturn(null);
        when(salvaContaPagamento.salvar(any(ContaPagamento.class))).thenReturn(contaSalva);
        
        // Act
        ContaPagamento resultado = criaContaPagamentoUseCase.criarOuBuscarPorEmail(
            nome, email, cidade, estado, pais, endereco, complemento, cep, phoneCode, phoneNumber
        );
        
        // Assert
        assertNotNull(resultado);
        assertEquals(2L, resultado.getId());
        assertEquals(email, resultado.getEmail());
        assertEquals(cidade, resultado.getBillAddrCity());
        assertEquals(nome, resultado.getName());
        
        verify(buscaContaPagamentoPorEmail, times(1)).buscarPorEmail(email);
        verify(salvaContaPagamento, times(1)).salvar(any(ContaPagamento.class));
    }
}
