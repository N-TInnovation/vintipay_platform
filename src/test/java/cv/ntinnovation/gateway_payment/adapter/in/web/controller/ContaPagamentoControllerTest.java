package cv.ntinnovation.gateway_payment.adapter.in.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cv.ntinnovation.gateway_payment.adapter.in.web.dto.ContaPagamentoRequest;
import cv.ntinnovation.gateway_payment.adapter.in.web.dto.ContaPagamentoResponse;
import cv.ntinnovation.gateway_payment.adapter.in.web.mapper.ContaPagamentoWebMapper;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.ContaPagamento;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.InfoContaPagamento;
import cv.ntinnovation.gateway_payment.application.port.in.CriaContaPagamentoUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ContaPagamentoController.class)
@AutoConfigureMockMvc(addFilters = false)  
class ContaPagamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CriaContaPagamentoUseCase criaContaPagamentoUseCase;

    @MockitoBean    
    private ContaPagamentoWebMapper contaPagamentoWebMapper;

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void criarContaPagamento_DeveRetornarSucesso() throws Exception {
        // Arrange
        ContaPagamentoRequest request = ContaPagamentoRequest.builder()
                .email("cliente@example.com")
                .billAddrCity("Praia")
                .billAddrCountry("CV")
                .billAddrLine1("Rua Principal, 123")
                .billAddrPostCode("12345")
                .build();

        ContaPagamento contaPagamento = createSampleContaPagamento();
        
        when(contaPagamentoWebMapper.toContaPagamento(any(ContaPagamentoRequest.class))).thenReturn(contaPagamento);
        when(criaContaPagamentoUseCase.criarOuBuscarPorEmail(
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString()
        )).thenReturn(contaPagamento);
        
        // Criando um objeto de resposta específico para o teste
        ContaPagamentoResponse mockResponse = ContaPagamentoResponse.builder()
                .id(1L)
                .email("cliente@example.com")
                .billAddrCity("Praia")
                .billAddrCountry("CV")
                .billAddrLine1("Rua Principal, 123")
                .billAddrPostCode("12345")
                .createdAt(LocalDateTime.now())
                .build();
                
        when(contaPagamentoWebMapper.toContaPagamentoResponse(any(ContaPagamento.class))).thenReturn(mockResponse);

        // Act & Assert
        ObjectMapper objectMapper = new ObjectMapper();
        mockMvc.perform(post("/api/v1/contas-pagamento")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("cliente@example.com"))
                .andExpect(jsonPath("$.billAddrCity").value("Praia"))
                .andExpect(jsonPath("$.billAddrCountry").value("CV"))
                .andExpect(jsonPath("$.billAddrLine1").value("Rua Principal, 123"))
                .andExpect(jsonPath("$.billAddrPostCode").value("12345"));
    }

    private ContaPagamento createSampleContaPagamento() {
        LocalDateTime agora = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String dataFormatada = agora.format(formatter);
        
        InfoContaPagamento acctInfo = InfoContaPagamento.builder()
                .chAccAgeInd("01")
                .chAccDate(dataFormatada)
                .chAccChange(dataFormatada)
                .chAccPwChange(dataFormatada)
                .chAccPwChangeInd("01")
                .suspiciousAccActivity("01")
                .build();
                
        return ContaPagamento.builder()
                .id(1L)
                .email("cliente@example.com")
                .billAddrCity("Praia")
                .billAddrCountry("CV")
                .billAddrLine1("Rua Principal, 123")
                .billAddrPostCode("12345")
                .acctInfo(acctInfo)
                .build();
    }
}
