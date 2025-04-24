package cv.ntinnovation.vintipay.integration;

import cv.ntinnovation.vintipay.application.core.domain.entity.PlanoAssinatura;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.port.in.GerenciaAssinaturaUseCase;
import cv.ntinnovation.vintipay.application.port.in.StripeIntegrationUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class StripeWebhookIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StripeIntegrationUseCase stripeIntegrationUseCase;

    @MockitoBean
    private GerenciaAssinaturaUseCase gerenciaAssinaturaUseCase;

    private final String WEBHOOK_URL = "/webhook/stripe";
    private final String STRIPE_SIGNATURE = "whsec_test_signature";

    @BeforeEach
    void setUp() {
        // Configuração padrão para o mock do StripeIntegrationUseCase
        Map<String, Object> defaultEventData = new HashMap<>();
        defaultEventData.put("eventId", "evt_default");
        when(stripeIntegrationUseCase.processarWebhook(any(), eq(STRIPE_SIGNATURE)))
                .thenReturn(defaultEventData);
    }

    @Test
    void testFluxoCompletoAssinaturaBemSucedida() throws Exception {
        // 1. Simular evento de checkout.session.completed
        simulateCheckoutSessionCompleted();
        
        // 2. Simular evento de customer.subscription.created
        simulateSubscriptionCreated();
        
        // 3. Simular evento de invoice.payment_succeeded
        simulatePaymentSucceeded();
        
        // Verificar que todas as chamadas esperadas foram feitas
        verify(gerenciaAssinaturaUseCase, times(1))
                .atualizarStatusAssinatura(eq("sub_test123"), eq(PlanoAssinatura.StatusAssinatura.ATIVA));
        
        verify(gerenciaAssinaturaUseCase, times(1))
                .atualizarStatusAssinatura(eq("sub_test456"), eq(PlanoAssinatura.StatusAssinatura.ATIVA));
        
        verify(gerenciaAssinaturaUseCase, times(1))
                .renovarAssinatura(eq("sub_invoice123"));
    }

    @Test
    void testFluxoAssinaturaComPeriodoTrial() throws Exception {
        // 1. Simular evento de checkout.session.completed
        simulateCheckoutSessionCompleted();
        
        // 2. Simular evento de customer.subscription.created com status trial
        simulateSubscriptionCreatedWithTrial();
        
        // Verificar que todas as chamadas esperadas foram feitas
        verify(gerenciaAssinaturaUseCase, times(1))
                .atualizarStatusAssinatura(eq("sub_test123"), eq(PlanoAssinatura.StatusAssinatura.ATIVA));
        
        verify(gerenciaAssinaturaUseCase, times(1))
                .atualizarStatusAssinatura(eq("sub_test456"), eq(PlanoAssinatura.StatusAssinatura.TRIAL));
    }

    @Test
    void testFluxoCancelamentoAssinatura() throws Exception {
        // 1. Simular evento de customer.subscription.deleted
        simulateSubscriptionDeleted();
        
        // Verificar que a chamada esperada foi feita
        verify(gerenciaAssinaturaUseCase, times(1))
                .atualizarStatusAssinatura(eq("sub_test456"), eq(PlanoAssinatura.StatusAssinatura.CANCELADA));
    }

    // Métodos auxiliares para simular os eventos

    private void simulateCheckoutSessionCompleted() throws Exception {
        String payload = getCheckoutSessionCompletedPayload();
        
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventType", "checkout.session.completed");
        eventData.put("eventId", "evt_test123");
        eventData.put("objectId", getCheckoutSessionObject());
        
        when(stripeIntegrationUseCase.processarWebhook(eq(payload), eq(STRIPE_SIGNATURE)))
                .thenReturn(eventData);
        
        mockMvc.perform(post(WEBHOOK_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Stripe-Signature", STRIPE_SIGNATURE)
                .content(payload))
                .andExpect(status().isOk());
    }

    private void simulateSubscriptionCreated() throws Exception {
        String payload = getSubscriptionCreatedPayload();
        
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventType", "customer.subscription.created");
        eventData.put("eventId", "evt_test456");
        eventData.put("objectId", getSubscriptionObject("active"));
        
        when(stripeIntegrationUseCase.processarWebhook(eq(payload), eq(STRIPE_SIGNATURE)))
                .thenReturn(eventData);
        
        mockMvc.perform(post(WEBHOOK_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Stripe-Signature", STRIPE_SIGNATURE)
                .content(payload))
                .andExpect(status().isOk());
    }

    private void simulateSubscriptionCreatedWithTrial() throws Exception {
        String payload = getSubscriptionCreatedPayload();
        
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventType", "customer.subscription.created");
        eventData.put("eventId", "evt_test789");
        eventData.put("objectId", getSubscriptionObject("trialing"));
        
        when(stripeIntegrationUseCase.processarWebhook(eq(payload), eq(STRIPE_SIGNATURE)))
                .thenReturn(eventData);
        
        mockMvc.perform(post(WEBHOOK_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Stripe-Signature", STRIPE_SIGNATURE)
                .content(payload))
                .andExpect(status().isOk());
    }

    private void simulatePaymentSucceeded() throws Exception {
        String payload = getPaymentSucceededPayload();
        
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventType", "invoice.payment_succeeded");
        eventData.put("eventId", "evt_invoice123");
        eventData.put("objectId", getInvoiceObject());
        
        when(stripeIntegrationUseCase.processarWebhook(eq(payload), eq(STRIPE_SIGNATURE)))
                .thenReturn(eventData);
        
        mockMvc.perform(post(WEBHOOK_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Stripe-Signature", STRIPE_SIGNATURE)
                .content(payload))
                .andExpect(status().isOk());
    }

    private void simulateSubscriptionDeleted() throws Exception {
        String payload = getSubscriptionDeletedPayload();
        
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventType", "customer.subscription.deleted");
        eventData.put("eventId", "evt_delete123");
        eventData.put("objectId", getSubscriptionObject("canceled"));
        
        when(stripeIntegrationUseCase.processarWebhook(eq(payload), eq(STRIPE_SIGNATURE)))
                .thenReturn(eventData);
        
        mockMvc.perform(post(WEBHOOK_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Stripe-Signature", STRIPE_SIGNATURE)
                .content(payload))
                .andExpect(status().isOk());
    }

    // Métodos auxiliares para gerar payloads de teste
    
    private String getCheckoutSessionCompletedPayload() {
        return "{ \"type\": \"checkout.session.completed\", \"data\": { \"object\": { ... } } }";
    }
    
    private String getCheckoutSessionObject() {
        return "{ \"object\": \"checkout.session\", \"id\": \"cs_test123\", \"customer\": \"cus_test123\", \"subscription\": \"sub_test123\", \"client_reference_id\": \"user_123\" }";
    }
    
    private String getSubscriptionCreatedPayload() {
        return "{ \"type\": \"customer.subscription.created\", \"data\": { \"object\": { ... } } }";
    }
    
    private String getSubscriptionObject(String status) {
        return "{ \"object\": \"subscription\", \"id\": \"sub_test456\", \"customer\": \"cus_test456\", \"status\": \"" + status + "\" }";
    }
    
    private String getPaymentSucceededPayload() {
        return "{ \"type\": \"invoice.payment_succeeded\", \"data\": { \"object\": { ... } } }";
    }
    
    private String getInvoiceObject() {
        return "{ \"object\": \"invoice\", \"id\": \"in_test123\", \"customer\": \"cus_invoice123\", \"subscription\": \"sub_invoice123\", \"paid\": true }";
    }
    
    private String getSubscriptionDeletedPayload() {
        return "{ \"type\": \"customer.subscription.deleted\", \"data\": { \"object\": { ... } } }";
    }
}
