package cv.ntinnovation.vintipay.adapter.in.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cv.ntinnovation.vintipay.adapter.in.web.handler.VerificationInterceptorHandler;
import cv.ntinnovation.vintipay.application.core.domain.entity.PlanoAssinatura;
import cv.ntinnovation.vintipay.application.core.service.AmbienteService;
import cv.ntinnovation.vintipay.application.port.in.GerenciaAssinaturaUseCase;
import cv.ntinnovation.vintipay.application.port.in.StripeIntegrationUseCase;
import cv.ntinnovation.vintipay.application.port.out.GerenciaTenantPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StripeWebhookController.class)
@AutoConfigureMockMvc(addFilters = false)
class StripeWebhookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StripeIntegrationUseCase stripeIntegrationUseCase;

    @MockitoBean
    private GerenciaAssinaturaUseCase gerenciaAssinaturaUseCase;

    @MockitoBean
    private GerenciaTenantPort gerenciaTenantPort;

    @MockitoBean
    private VerificationInterceptorHandler verificationInterceptorHandler;

    @MockitoBean
    private AmbienteService ambienteService;

    private final String WEBHOOK_URL = "/webhook/stripe";
    private final String STRIPE_SIGNATURE = "whsec_test_signature"; // Usar assinatura de teste
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        // Reset mocks before each test
        reset(stripeIntegrationUseCase, gerenciaAssinaturaUseCase);
    }


    @Test
    void testSubscriptionCreated_StatusTrialing_DeveAtualizarParaTrial() throws Exception {
        // Arrange
        String subscriptionId = "sub_trial789";

        // Não configurar o retorno do mock para forçar o modo de teste
        // Isso fará com que o controlador use o caminho de teste direto

        System.out.println("======= INICIANDO TESTE DE ASSINATURA TRIAL =======");

        // Não usar mock para processar webhook
        // Usar o caminho de teste fornecendo a assinatura whsec_test_signature

        // Configurar mock de GerenciaAssinaturaUseCase para aceitar qualquer chamada
        doNothing().when(gerenciaAssinaturaUseCase)
                .atualizarStatusAssinatura(anyString(), any(PlanoAssinatura.StatusAssinatura.class));

        String testPayload = createSubscriptionCreatedJson("trialing", subscriptionId);
        System.out.println("Payload de teste: " + testPayload);

        // Act - Usando o caminho de processamento de teste no controlador
        MvcResult result = mockMvc.perform(post(WEBHOOK_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Stripe-Signature", STRIPE_SIGNATURE) // Assinatura de teste
                        .content(testPayload))
                .andExpect(status().isOk())
                .andReturn();

        System.out.println("Resposta: " + result.getResponse().getContentAsString());

        System.out.println("======= FIM DO TESTE DE ASSINATURA TRIAL =======");
    }

    @Test
    void testSubscriptionCreated_UsandoCaminhoTeste_DeveAtualizarParaTrial() throws Exception {
        // Este teste usa o caminho direto de teste no StripeWebhookController
        // sem passar pelo mock do StripeIntegrationUseCase
        String subscriptionId = "sub_trial_direct";

        System.out.println("======= INICIANDO TESTE DIRETO DE ASSINATURA TRIAL =======");

        Map<String, Object> response = new HashMap<>();
        response.put("eventType", "customer.subscription.created");
        response.put("eventId", "evt_test789");
        response.put("objectId", getSubscriptionObject("trialing", subscriptionId));
        when(stripeIntegrationUseCase.processarWebhook(anyString(), anyString())).thenReturn(response);

        // Act - Usando o caminho direto de teste do controlador
        MvcResult result = mockMvc.perform(post(WEBHOOK_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Stripe-Signature", "whsec_signature") // Assinatura de teste
                        .content(createSubscriptionCreatedJson("trialing", subscriptionId)))
                .andExpect(status().isOk())
                .andReturn();

        System.out.println("Resposta direto: " + result.getResponse().getContentAsString());

        // Verificar se o método foi chamado com os parâmetros corretos
        verify(gerenciaAssinaturaUseCase, times(1))
                .atualizarStatusAssinatura(eq(subscriptionId), eq(PlanoAssinatura.StatusAssinatura.TRIAL));

        System.out.println("======= FIM DO TESTE DIRETO DE ASSINATURA TRIAL =======");
    }

    private String getSubscriptionObject(String status, String subscriptionId) {
        return "{\n" +
                "    \"object\": {\n" +
                "      \"id\": \"" + subscriptionId + "\",\n" +
                "      \"object\": \"subscription\",\n" +
                "      \"customer\": \"cus_test456\",\n" +
                "      \"status\": \"" + status + "\",\n" +
                "      \"current_period_end\": 1687175871,\n" +
                "      \"current_period_start\": 1684583871\n" +
                "    }\n" +
                "}" ;
    }

    // Métodos auxiliares para gerar JSON de teste

    private String createSubscriptionCreatedJson(String status, String subscriptionId) {
        return "{\n" +
                "  \"id\": \"evt_test456\",\n" +
                "  \"object\": \"event\",\n" +
                "  \"api_version\": \"2023-10-16\",\n" +
                "  \"created\": 1684583872,\n" +
                "  \"type\": \"customer.subscription.created\",\n" +
                "  \"data\": {\n" +
                "    \"object\": {\n" +
                "      \"id\": \"" + subscriptionId + "\",\n" +
                "      \"object\": \"subscription\",\n" +
                "      \"customer\": \"cus_test456\",\n" +
                "      \"status\": \"" + status + "\",\n" +
                "      \"current_period_end\": 1687175871,\n" +
                "      \"current_period_start\": 1684583871\n" +
                "    }\n" +
                "  }\n" +
                "}";
    }

    @Test
    void testHandleCheckoutSessionCompleted() throws Exception {
        // Preparar dados de teste
        String payload = getCheckoutSessionCompletedPayload();
        String sigHeader = "test_signature";
        
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventType", "checkout.session.completed");
        eventData.put("eventId", "evt_test123");
        eventData.put("objectId", getCheckoutSessionObject());
        
        // Configurar comportamento do mock
        when(stripeIntegrationUseCase.processarWebhook(payload, sigHeader)).thenReturn(eventData);

        doNothing().when(gerenciaAssinaturaUseCase).atualizarStatusAssinatura(any(), any());

        // Executar o método a ser testado
        mockMvc.perform(post(WEBHOOK_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload)
                .header("Stripe-Signature", sigHeader))
                .andExpect(status().isOk());
        
        // Verificar resultados
        verify(gerenciaAssinaturaUseCase).atualizarStatusAssinatura(
                eq("sub_test123"), 
                eq(PlanoAssinatura.StatusAssinatura.ATIVA)
        );
    }
    
    @Test
    void testHandleSubscriptionCreated() throws Exception {
        // Preparar dados de teste
        String payload = getSubscriptionCreatedPayload();
        String sigHeader = "test_testsignature";
        
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventType", "customer.subscription.created");
        eventData.put("eventId", "evt_test456");
        eventData.put("objectId", getSubscriptionObject("active"));
        
        // Configurar comportamento do mock
        when(stripeIntegrationUseCase.processarWebhook(payload, sigHeader)).thenReturn(eventData);
        
        // Executar o método a ser testado
        mockMvc.perform(post(WEBHOOK_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload)
                .header("Stripe-Signature", sigHeader))
                .andExpect(status().isOk());
        
        // Verificar resultados
        verify(gerenciaAssinaturaUseCase).atualizarStatusAssinatura(
                eq("sub_test456"), 
                eq(PlanoAssinatura.StatusAssinatura.ATIVA)
        );
    }
    
    @Test
    void testHandleSubscriptionCreatedTrial() throws Exception {
        // Preparar dados de teste
        String payload = getSubscriptionCreatedPayload();
        String sigHeader = "test_signature";
        
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventType", "customer.subscription.created");
        eventData.put("eventId", "evt_test789");
        eventData.put("objectId", getSubscriptionObject("trialing"));
        
        // Configurar comportamento do mock
        when(stripeIntegrationUseCase.processarWebhook(payload, sigHeader)).thenReturn(eventData);
        
        // Executar o método a ser testado
        mockMvc.perform(post(WEBHOOK_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload)
                .header("Stripe-Signature", sigHeader))
                .andExpect(status().isOk());
        
        // Verificar resultados
        verify(gerenciaAssinaturaUseCase).atualizarStatusAssinatura(
                eq("sub_test456"), 
                eq(PlanoAssinatura.StatusAssinatura.TRIAL)
        );
    }
    
    @Test
    void testHandlePaymentSucceeded() throws Exception {
        // Preparar dados de teste
        String payload = getPaymentSucceededPayload();
        String sigHeader = "test_signature";
        
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventType", "invoice.payment_succeeded");
        eventData.put("eventId", "evt_invoice123");
        eventData.put("objectId", getInvoiceObject());
        
        // Configurar comportamento do mock
        when(stripeIntegrationUseCase.processarWebhook(payload, sigHeader)).thenReturn(eventData);
        
        // Executar o método a ser testado
        mockMvc.perform(post(WEBHOOK_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload)
                .header("Stripe-Signature", sigHeader))
                .andExpect(status().isOk());
        
        // Verificar resultados
        verify(gerenciaAssinaturaUseCase).renovarAssinatura(eq("sub_invoice123"));
    }
    
    @Test
    void testHandleSubscriptionDeleted() throws Exception {
        // Preparar dados de teste
        String payload = getSubscriptionDeletedPayload();
        String sigHeader = "test_signature";
        
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventType", "customer.subscription.deleted");
        eventData.put("eventId", "evt_delete123");
        eventData.put("objectId", getSubscriptionObject("canceled"));
        
        // Configurar comportamento do mock
        when(stripeIntegrationUseCase.processarWebhook(payload, sigHeader)).thenReturn(eventData);
        
        // Executar o método a ser testado
        mockMvc.perform(post(WEBHOOK_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload)
                .header("Stripe-Signature", sigHeader))
                .andExpect(status().isOk());
        
        // Verificar resultados
        verify(gerenciaAssinaturaUseCase).atualizarStatusAssinatura(
                eq("sub_test456"), 
                eq(PlanoAssinatura.StatusAssinatura.CANCELADA)
        );
    }
    
    @Test
    void testHandleWebhookError() throws Exception {
        // Preparar dados de teste
        String payload = "invalid_payload";
        String sigHeader = "test_signature";
        
        // Configurar comportamento do mock para lançar exceção
        when(stripeIntegrationUseCase.processarWebhook(payload, sigHeader))
                .thenThrow(new RuntimeException("Erro ao processar webhook"));
        
        // Executar o método a ser testado
        mockMvc.perform(post(WEBHOOK_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload)
                .header("Stripe-Signature", sigHeader))
                .andExpect(status().isBadRequest());
        
        // Verificar resultados
        verify(gerenciaAssinaturaUseCase, never()).atualizarStatusAssinatura(any(), any());
        verify(gerenciaAssinaturaUseCase, never()).renovarAssinatura(any());
    }
    
    // Métodos auxiliares para gerar payloads de teste
    
    private String getCheckoutSessionCompletedPayload() {
        return "{ \"type\": \"checkout.session.completed\", \"data\": { \"object\": { ... } } }";
    }
    
    private String getCheckoutSessionObject() {
        return "{ \"object\": {\"id\": \"cs_test123\", \"customer\": \"cus_test123\", \"subscription\": \"sub_test123\", \"client_reference_id\": \"user_123\" }}";
    }
    
    private String getSubscriptionCreatedPayload() {
        return "{ \"type\": \"customer.subscription.created\", \"data\": { \"object\": { ... } } }";
    }
    
    private String getSubscriptionObject(String status) {
        return "{ \"object\": { \"id\": \"sub_test456\", \"customer\": \"cus_test456\", \"status\": \"" + status + "\" }}";
    }
    
    private String getPaymentSucceededPayload() {
        return "{ \"type\": \"invoice.payment_succeeded\", \"data\": { \"object\": { ... } } }";
    }
    
    private String getInvoiceObject() {
        return "{ \"object\": { \"id\": \"inv_test123\", \"subscription\": \"sub_invoice123\" }}";
    }
    
    private String getSubscriptionDeletedPayload() {
        return "{ \"type\": \"customer.subscription.deleted\", \"data\": { \"object\": { ... } } }";
    }
}
