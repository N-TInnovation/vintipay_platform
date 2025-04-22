package cv.ntinnovation.vintipay.adapter.in.web.controller;

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

    @PostMapping
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            // Processar o webhook
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
                    break;
                    
                case "customer.subscription.created":
                    // Processar criação de assinatura
                    log.info("Assinatura criada: {}", objectJson);
                    break;
                    
                case "customer.subscription.updated":
                    // Processar atualização de assinatura
                    log.info("Assinatura atualizada: {}", objectJson);
                    break;
                    
                case "invoice.payment_succeeded":
                    // Processar pagamento bem-sucedido
                    log.info("Pagamento bem-sucedido: {}", objectJson);
                    break;
                    
                case "customer.subscription.deleted":
                    // Processar cancelamento de assinatura
                    log.info("Assinatura cancelada: {}", objectJson);
                    // Atualizar status da assinatura no sistema
                    try {
                        // Extrair ID da assinatura do evento
                        String subscriptionId = extractSubscriptionId(objectJson);
                        if (subscriptionId != null) {
                            gerenciaAssinaturaUseCase.atualizarStatusAssinatura(
                                    subscriptionId, 
                                    PlanoAssinatura.StatusAssinatura.CANCELADA
                            );
                        }
                    } catch (Exception e) {
                        log.error("Erro ao processar cancelamento de assinatura", e);
                    }
                    break;
                    
                default:
                    log.info("Evento não processado: {}", eventType);
            }
            
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Erro ao processar webhook do Stripe", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Webhook Error: " + e.getMessage());
        }
    }
    
    private String extractSubscriptionId(String json) {
        // Implementação simplificada - em um ambiente real, você usaria um parser JSON adequado
        if (json.contains("\"id\":")) {
            int startIndex = json.indexOf("\"id\":") + 6;
            int endIndex = json.indexOf("\"", startIndex);
            if (startIndex > 0 && endIndex > startIndex) {
                return json.substring(startIndex, endIndex);
            }
        }
        return null;
    }
}
