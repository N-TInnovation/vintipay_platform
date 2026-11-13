package cv.ntinnovation.vintipay.adapter.in.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cv.ntinnovation.vintipay.adapter.in.web.dto.ContaPagamentoRequest;
import cv.ntinnovation.vintipay.adapter.in.web.dto.ContaPagamentoResponse;
import cv.ntinnovation.vintipay.adapter.in.web.handler.VerificationInterceptorHandler;
import cv.ntinnovation.vintipay.adapter.in.web.mapper.ContaPagamentoWebMapper;
import cv.ntinnovation.vintipay.application.core.domain.entity.ContaPagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.ContatoContaPagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.InfoContaPagamento;
import cv.ntinnovation.vintipay.application.core.service.AmbienteService;
import cv.ntinnovation.vintipay.application.port.in.CriaContaPagamentoUseCase;
import cv.ntinnovation.vintipay.application.port.out.GerenciaTenantPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
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

    @MockitoBean
    private GerenciaTenantPort gerenciaTenantPort;

    @MockitoBean
    private VerificationInterceptorHandler verificationInterceptorHandler;

    @MockitoBean
    private AmbienteService ambienteService;
    
    private final String CONTA_PAGAMENTO_URL = "/api/v1/contas-pagamento";
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @BeforeEach
    void setUp() {
        // Reset mocks before each test
        reset(criaContaPagamentoUseCase, contaPagamentoWebMapper);
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void criarContaPagamento_DeveRetornarSucesso() throws Exception {
        // Arrange
        System.out.println("======= INICIANDO TESTE CRIAR CONTA PAGAMENTO =======");
        
        ContaPagamentoRequest request = ContaPagamentoRequest.builder()
                .name("João Silva")
                .email("cliente@example.com")
                .billAddrCity("Praia")
                .billAddrState("Ilha de Santiago")
                .billAddrCountry("CV")
                .billAddrLine1("Rua Principal, 123")
                .billAddrLine2("Apto 101")
                .billAddrPostCode("12345")
                .phoneCode("+238")
                .phoneNumber("9999999")
                .build();

        ContaPagamento contaPagamento = createSampleContaPagamento();

        when(criaContaPagamentoUseCase.criarOuBuscarPorEmail(
                anyString(), anyString(), anyString(), anyString(), anyString(), 
                anyString(), anyString(), anyString(), anyString(), anyString()
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

        // Act
        MvcResult result = mockMvc.perform(post(CONTA_PAGAMENTO_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("cliente@example.com"))
                .andExpect(jsonPath("$.billAddrCity").value("Praia"))
                .andExpect(jsonPath("$.billAddrCountry").value("CV"))
                .andExpect(jsonPath("$.billAddrLine1").value("Rua Principal, 123"))
                .andExpect(jsonPath("$.billAddrPostCode").value("12345"))
                .andReturn();

        
        // Verifica que os métodos foram chamados com os parâmetros corretos

        verify(criaContaPagamentoUseCase, times(1)).criarOuBuscarPorEmail(
            anyString(), anyString(), anyString(), anyString(), anyString(), 
            anyString(), anyString(), anyString(), anyString(), anyString()
        );
        verify(contaPagamentoWebMapper, times(1)).toContaPagamentoResponse(any(ContaPagamento.class));
        
        System.out.println("======= FIM DO TESTE CRIAR CONTA PAGAMENTO =======");
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
                
        ContatoContaPagamento mobilePhone = ContatoContaPagamento.builder()
                .cc("+238")
                .subscriber("9999999")
                .build();
                
        return ContaPagamento.builder()
                .id(1L)
                .name("João Silva")
                .email("cliente@example.com")
                .billAddrCity("Praia")
                .billAddrState("Ilha de Santiago")
                .billAddrCountry("CV")
                .billAddrLine1("Rua Principal, 123")
                .billAddrLine2("Apto 101")
                .billAddrPostCode("12345")
                .mobilePhone(mobilePhone)
                .acctInfo(acctInfo)
                .build();
    }
}
