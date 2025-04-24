package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.application.core.domain.entity.PlanoAssinatura;
import cv.ntinnovation.vintipay.application.port.in.GerenciaAssinaturaUseCase;
import cv.ntinnovation.vintipay.application.port.in.StripeIntegrationUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class StripeWebhookControllerTest {

    @Mock
    private StripeIntegrationUseCase stripeIntegrationUseCase;

    @Mock
    private GerenciaAssinaturaUseCase gerenciaAssinaturaUseCase;

    @InjectMocks
    private StripeWebhookController stripeWebhookController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHandleCheckoutSessionCompleted() {
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
        ResponseEntity<String> response = stripeWebhookController.handleStripeWebhook(payload, sigHeader);
        
        // Verificar resultados
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(gerenciaAssinaturaUseCase).atualizarStatusAssinatura(
                eq("sub_test123"), 
                eq(PlanoAssinatura.StatusAssinatura.ATIVA)
        );
    }
    
    @Test
    void testHandleSubscriptionCreated() {
        // Preparar dados de teste
        String payload = getSubscriptionCreatedPayload();
        String sigHeader = "test_signature";
        
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventType", "customer.subscription.created");
        eventData.put("eventId", "evt_test456");
        eventData.put("objectId", getSubscriptionObject("active"));
        
        // Configurar comportamento do mock
        when(stripeIntegrationUseCase.processarWebhook(payload, sigHeader)).thenReturn(eventData);
        
        // Executar o método a ser testado
        ResponseEntity<String> response = stripeWebhookController.handleStripeWebhook(payload, sigHeader);
        
        // Verificar resultados
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(gerenciaAssinaturaUseCase).atualizarStatusAssinatura(
                eq("sub_test456"), 
                eq(PlanoAssinatura.StatusAssinatura.ATIVA)
        );
    }
    
    @Test
    void testHandleSubscriptionCreatedTrial() {
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
        ResponseEntity<String> response = stripeWebhookController.handleStripeWebhook(payload, sigHeader);
        
        // Verificar resultados
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(gerenciaAssinaturaUseCase).atualizarStatusAssinatura(
                eq("sub_test456"), 
                eq(PlanoAssinatura.StatusAssinatura.TRIAL)
        );
    }
    
    @Test
    void testHandlePaymentSucceeded() {
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
        ResponseEntity<String> response = stripeWebhookController.handleStripeWebhook(payload, sigHeader);
        
        // Verificar resultados
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(gerenciaAssinaturaUseCase).renovarAssinatura(eq("sub_invoice123"));
    }
    
    @Test
    void testHandleSubscriptionDeleted() {
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
        ResponseEntity<String> response = stripeWebhookController.handleStripeWebhook(payload, sigHeader);
        
        // Verificar resultados
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(gerenciaAssinaturaUseCase).atualizarStatusAssinatura(
                eq("sub_test456"), 
                eq(PlanoAssinatura.StatusAssinatura.CANCELADA)
        );
    }
    
    @Test
    void testHandleWebhookError() {
        // Preparar dados de teste
        String payload = "invalid_payload";
        String sigHeader = "test_signature";
        
        // Configurar comportamento do mock para lançar exceção
        when(stripeIntegrationUseCase.processarWebhook(payload, sigHeader))
                .thenThrow(new RuntimeException("Erro ao processar webhook"));
        
        // Executar o método a ser testado
        ResponseEntity<String> response = stripeWebhookController.handleStripeWebhook(payload, sigHeader);
        
        // Verificar resultados
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(gerenciaAssinaturaUseCase, never()).atualizarStatusAssinatura(any(), any());
        verify(gerenciaAssinaturaUseCase, never()).renovarAssinatura(any());
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
