package cv.ntinnovation.vintipay.adapter.in.web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cv.ntinnovation.vintipay.application.core.domain.entity.PlanoAssinatura;
import cv.ntinnovation.vintipay.application.port.in.GerenciaAssinaturaUseCase;
import cv.ntinnovation.vintipay.application.port.in.StripeIntegrationUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/webhook/stripe")
@RequiredArgsConstructor
public class StripeWebhookController {

    private final StripeIntegrationUseCase stripeIntegrationUseCase;
    private final GerenciaAssinaturaUseCase gerenciaAssinaturaUseCase;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "Stripe-Signature", required = false) String sigHeader) {
        try {
            log.info("Webhook do Stripe recebido! Payload: {}, Signature: {}", payload, sigHeader);
            
            // Se estamos em modo de teste e não temos uma assinatura válida, processamos diretamente
            if (sigHeader == null || sigHeader.equals("whsec_test_signature")) {
                log.info("Modo de teste detectado - processando webhook sem verificação de assinatura");
                
                try {
                    JsonNode rootNode = objectMapper.readTree(payload);
                    String eventType = rootNode.path("type").asText();
                    String eventId = rootNode.path("id").asText();
                    JsonNode dataObject = rootNode.path("data").path("object");
                    String objectJson = dataObject.toString();
                    
                    log.info("Evento Stripe de teste recebido: {}, ID: {}", eventType, eventId);
                    
                    // Processar diferentes tipos de eventos
                    switch (eventType) {
                        case "checkout.session.completed":
                            log.info("Checkout completo (teste): {}", objectJson);
                            processTestCheckoutSession(dataObject);
                            break;
                            
                        case "customer.subscription.created":
                            log.info("Assinatura criada (teste): {}", objectJson);
                            processTestSubscriptionCreated(dataObject);
                            break;
                            
                        case "customer.subscription.updated":
                            log.info("Assinatura atualizada (teste): {}", objectJson);
                            processTestSubscriptionUpdated(dataObject);
                            break;
                            
                        case "invoice.payment_succeeded":
                            log.info("Pagamento bem-sucedido (teste): {}", objectJson);
                            processTestPaymentSucceeded(dataObject);
                            break;
                            
                        case "customer.subscription.deleted":
                            log.info("Assinatura cancelada (teste): {}", objectJson);
                            processTestSubscriptionDeleted(dataObject);
                            break;
                            
                        default:
                            log.info("Evento não processado (teste): {}", eventType);
                    }
                    
                    return ResponseEntity.ok("Webhook de teste processado com sucesso: " + eventType);
                } catch (Exception e) {
                    log.error("Erro ao processar webhook de teste", e);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Erro ao processar webhook de teste: " + e.getMessage());
                }
            }
            
            // Processar o webhook normalmente com verificação de assinatura
            Map<String, Object> eventData = stripeIntegrationUseCase.processarWebhook(payload, sigHeader);
            String eventType = (String) eventData.get("eventType");
            String eventId = (String) eventData.get("eventId");
            String objectJson = (String) eventData.get("objectId");
            
            log.info("Evento Stripe recebido: {}, ID: {}", eventType, eventId);
            
            // Processar diferentes tipos de eventos
            switch (eventType) {
                case "checkout.session.completed":
                    // Processar checkout completo
                    log.info("Checkout completo: {}", objectJson);
                    handleCheckoutSessionCompleted(objectJson);
                    break;
                    
                case "customer.subscription.created":
                    // Processar criação de assinatura
                    log.info("Assinatura criada: {}", objectJson);
                    handleSubscriptionCreated(objectJson);
                    break;
                    
                case "customer.subscription.updated":
                    // Processar atualização de assinatura
                    log.info("Assinatura atualizada: {}", objectJson);
                    handleSubscriptionUpdated(objectJson);
                    break;
                    
                case "invoice.payment_succeeded":
                    // Processar pagamento bem-sucedido
                    log.info("Pagamento bem-sucedido: {}", objectJson);
                    handlePaymentSucceeded(objectJson);
                    break;
                    
                case "customer.subscription.deleted":
                    // Processar cancelamento de assinatura
                    log.info("Assinatura cancelada: {}", objectJson);
                    handleSubscriptionDeleted(objectJson);
                    break;
                    
                default:
                    log.info("Evento não processado: {}", eventType);
            }
            
            return ResponseEntity.ok("Webhook processado com sucesso: " + eventType);
        } catch (Exception e) {
            log.error("Erro ao processar webhook do Stripe", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Webhook Error: " + e.getMessage());
        }
    }
    
    /**
     * Processa o evento de checkout.session.completed em modo de teste
     */
    private void processTestCheckoutSession(JsonNode sessionNode) {
        try {
            String subscriptionId = sessionNode.path("subscription").asText(null);
            String customerId = sessionNode.path("customer").asText(null);
            String clientReferenceId = sessionNode.path("client_reference_id").asText(null);
            
            log.info("Checkout completo (teste) - Subscription ID: {}, Customer ID: {}, Reference ID: {}", 
                    subscriptionId, customerId, clientReferenceId);
            
            if (subscriptionId != null && !subscriptionId.isEmpty()) {
                // Simular atualização do status da assinatura
                log.info("Simulando atualização de status da assinatura para ATIVA: {}", subscriptionId);
            }
        } catch (Exception e) {
            log.error("Erro ao processar evento de checkout completo (teste)", e);
        }
    }
    
    /**
     * Processa o evento de customer.subscription.created em modo de teste
     */
    private void processTestSubscriptionCreated(JsonNode subscriptionNode) {
        try {
            String subscriptionId = subscriptionNode.path("id").asText(null);
            String status = subscriptionNode.path("status").asText();
            
            log.info("Assinatura criada (teste) - ID: {}, Status: {}", subscriptionId, status);
            
            if (subscriptionId != null && !subscriptionId.isEmpty()) {
                // Simular mapeamento do status
                PlanoAssinatura.StatusAssinatura assinaturaStatus;
                
                switch (status) {
                    case "active":
                        assinaturaStatus = PlanoAssinatura.StatusAssinatura.ATIVA;
                        break;
                    case "trialing":
                        assinaturaStatus = PlanoAssinatura.StatusAssinatura.TRIAL;
                        break;
                    case "canceled":
                    case "unpaid":
                    case "past_due":
                        assinaturaStatus = PlanoAssinatura.StatusAssinatura.CANCELADA;
                        break;
                    default:
                        assinaturaStatus = PlanoAssinatura.StatusAssinatura.PENDENTE;
                }
                
                log.info("Simulando atualização de status da assinatura: {} -> {}", subscriptionId, assinaturaStatus);
            }
        } catch (Exception e) {
            log.error("Erro ao processar evento de criação de assinatura (teste)", e);
        }
    }
    
    /**
     * Processa o evento de customer.subscription.updated em modo de teste
     */
    private void processTestSubscriptionUpdated(JsonNode subscriptionNode) {
        try {
            String subscriptionId = subscriptionNode.path("id").asText(null);
            String status = subscriptionNode.path("status").asText();
            
            log.info("Assinatura atualizada (teste) - ID: {}, Status: {}", subscriptionId, status);
            
            if (subscriptionId != null && !subscriptionId.isEmpty()) {
                // Simular mapeamento do status
                PlanoAssinatura.StatusAssinatura assinaturaStatus;
                
                switch (status) {
                    case "active":
                        assinaturaStatus = PlanoAssinatura.StatusAssinatura.ATIVA;
                        break;
                    case "trialing":
                        assinaturaStatus = PlanoAssinatura.StatusAssinatura.TRIAL;
                        break;
                    case "canceled":
                    case "unpaid":
                    case "past_due":
                        assinaturaStatus = PlanoAssinatura.StatusAssinatura.CANCELADA;
                        break;
                    default:
                        assinaturaStatus = PlanoAssinatura.StatusAssinatura.PENDENTE;
                }
                
                log.info("Simulando atualização de status da assinatura: {} -> {}", subscriptionId, assinaturaStatus);
            }
        } catch (Exception e) {
            log.error("Erro ao processar evento de atualização de assinatura (teste)", e);
        }
    }
    
    /**
     * Processa o evento de invoice.payment_succeeded em modo de teste
     */
    private void processTestPaymentSucceeded(JsonNode invoiceNode) {
        try {
            String subscriptionId = invoiceNode.path("subscription").asText(null);
            String customerId = invoiceNode.path("customer").asText(null);
            String invoiceId = invoiceNode.path("id").asText(null);
            
            log.info("Pagamento bem-sucedido (teste) - Invoice ID: {}, Subscription ID: {}, Customer ID: {}", 
                    invoiceId, subscriptionId, customerId);
            
            if (subscriptionId != null && !subscriptionId.isEmpty()) {
                log.info("Simulando renovação da assinatura: {}", subscriptionId);
            }
        } catch (Exception e) {
            log.error("Erro ao processar evento de pagamento bem-sucedido (teste)", e);
        }
    }
    
    /**
     * Processa o evento de customer.subscription.deleted em modo de teste
     */
    private void processTestSubscriptionDeleted(JsonNode subscriptionNode) {
        try {
            String subscriptionId = subscriptionNode.path("id").asText(null);
            
            log.info("Assinatura cancelada (teste) - ID: {}", subscriptionId);
            
            if (subscriptionId != null && !subscriptionId.isEmpty()) {
                log.info("Simulando atualização de status da assinatura para CANCELADA: {}", subscriptionId);
            }
        } catch (Exception e) {
            log.error("Erro ao processar evento de cancelamento de assinatura (teste)", e);
        }
    }
    
    /**
     * Processa o evento de checkout.session.completed
     * Este evento ocorre quando um cliente completa o fluxo de checkout
     */
    private void handleCheckoutSessionCompleted(String jsonData) {
        try {
            JsonNode rootNode = objectMapper.readTree(jsonData);
            JsonNode sessionNode = rootNode.path("object");
            
            String subscriptionId = sessionNode.path("subscription").asText(null);
            String customerId = sessionNode.path("customer").asText(null);
            String clientReferenceId = sessionNode.path("client_reference_id").asText(null);
            
            log.info("Checkout completo - Subscription ID: {}, Customer ID: {}, Reference ID: {}", 
                    subscriptionId, customerId, clientReferenceId);
            
            if (subscriptionId != null && !subscriptionId.isEmpty()) {
                // Atualizar o status da assinatura para ATIVA
                gerenciaAssinaturaUseCase.atualizarStatusAssinatura(
                        subscriptionId, 
                        PlanoAssinatura.StatusAssinatura.ATIVA
                );
                log.info("Assinatura ativada com sucesso: {}", subscriptionId);
            } else {
                log.warn("ID de assinatura não encontrado no evento de checkout completo");
            }
        } catch (Exception e) {
            log.error("Erro ao processar evento de checkout completo", e);
        }
    }
    
    /**
     * Processa o evento de customer.subscription.created
     * Este evento ocorre quando uma nova assinatura é criada
     */
    private void handleSubscriptionCreated(String jsonData) {
        try {
            JsonNode rootNode = objectMapper.readTree(jsonData);
            JsonNode subscriptionNode = rootNode.path("object");
            
            String subscriptionId = subscriptionNode.path("id").asText(null);
            String status = subscriptionNode.path("status").asText();
            
            log.info("Assinatura criada - ID: {}, Status: {}", subscriptionId, status);
            
            if (subscriptionId != null && !subscriptionId.isEmpty()) {
                PlanoAssinatura.StatusAssinatura assinaturaStatus;
                
                // Mapear o status do Stripe para o status interno
                switch (status) {
                    case "active":
                        assinaturaStatus = PlanoAssinatura.StatusAssinatura.ATIVA;
                        break;
                    case "trialing":
                        assinaturaStatus = PlanoAssinatura.StatusAssinatura.TRIAL;
                        break;
                    case "canceled":
                    case "unpaid":
                    case "past_due":
                        assinaturaStatus = PlanoAssinatura.StatusAssinatura.CANCELADA;
                        break;
                    default:
                        assinaturaStatus = PlanoAssinatura.StatusAssinatura.PENDENTE;
                }
                
                // Atualizar o status da assinatura
                gerenciaAssinaturaUseCase.atualizarStatusAssinatura(
                        subscriptionId, 
                        assinaturaStatus
                );
                log.info("Status da assinatura atualizado: {} -> {}", subscriptionId, assinaturaStatus);
            }
        } catch (Exception e) {
            log.error("Erro ao processar evento de criação de assinatura", e);
        }
    }
    
    /**
     * Processa o evento de customer.subscription.updated
     * Este evento ocorre quando uma assinatura existente é atualizada
     */
    private void handleSubscriptionUpdated(String jsonData) {
        try {
            JsonNode rootNode = objectMapper.readTree(jsonData);
            JsonNode subscriptionNode = rootNode.path("object");
            
            String subscriptionId = subscriptionNode.path("id").asText(null);
            String status = subscriptionNode.path("status").asText();
            
            log.info("Assinatura atualizada - ID: {}, Status: {}", subscriptionId, status);
            
            if (subscriptionId != null && !subscriptionId.isEmpty()) {
                PlanoAssinatura.StatusAssinatura assinaturaStatus;
                
                // Mapear o status do Stripe para o status interno
                switch (status) {
                    case "active":
                        assinaturaStatus = PlanoAssinatura.StatusAssinatura.ATIVA;
                        break;
                    case "trialing":
                        assinaturaStatus = PlanoAssinatura.StatusAssinatura.TRIAL;
                        break;
                    case "canceled":
                    case "unpaid":
                    case "past_due":
                        assinaturaStatus = PlanoAssinatura.StatusAssinatura.CANCELADA;
                        break;
                    default:
                        assinaturaStatus = PlanoAssinatura.StatusAssinatura.PENDENTE;
                }
                
                // Atualizar o status da assinatura
                gerenciaAssinaturaUseCase.atualizarStatusAssinatura(
                        subscriptionId, 
                        assinaturaStatus
                );
                log.info("Status da assinatura atualizado: {} -> {}", subscriptionId, assinaturaStatus);
            }
        } catch (Exception e) {
            log.error("Erro ao processar evento de atualização de assinatura", e);
        }
    }
    
    /**
     * Processa o evento de invoice.payment_succeeded
     * Este evento ocorre quando um pagamento de fatura é bem-sucedido
     */
    private void handlePaymentSucceeded(String jsonData) {
        try {
            JsonNode rootNode = objectMapper.readTree(jsonData);
            JsonNode invoiceNode = rootNode.path("object");
            
            String subscriptionId = invoiceNode.path("subscription").asText(null);
            String customerId = invoiceNode.path("customer").asText(null);
            String invoiceId = invoiceNode.path("id").asText(null);
            
            log.info("Pagamento bem-sucedido - Invoice ID: {}, Subscription ID: {}, Customer ID: {}", 
                    invoiceId, subscriptionId, customerId);
            
            if (subscriptionId != null && !subscriptionId.isEmpty()) {
                // Renovar a assinatura
                gerenciaAssinaturaUseCase.renovarAssinatura(subscriptionId);
                log.info("Assinatura renovada com sucesso: {}", subscriptionId);
            }
        } catch (Exception e) {
            log.error("Erro ao processar evento de pagamento bem-sucedido", e);
        }
    }
    
    /**
     * Processa o evento de customer.subscription.deleted
     * Este evento ocorre quando uma assinatura é cancelada
     */
    private void handleSubscriptionDeleted(String jsonData) {
        try {
            JsonNode rootNode = objectMapper.readTree(jsonData);
            JsonNode subscriptionNode = rootNode.path("object");
            
            String subscriptionId = subscriptionNode.path("id").asText(null);
            
            log.info("Assinatura cancelada - ID: {}", subscriptionId);
            
            if (subscriptionId != null && !subscriptionId.isEmpty()) {
                // Atualizar o status da assinatura para CANCELADA
                gerenciaAssinaturaUseCase.atualizarStatusAssinatura(
                        subscriptionId, 
                        PlanoAssinatura.StatusAssinatura.CANCELADA
                );
                log.info("Status da assinatura atualizado para CANCELADA: {}", subscriptionId);
            }
        } catch (Exception e) {
            log.error("Erro ao processar evento de cancelamento de assinatura", e);
        }
    }
}
