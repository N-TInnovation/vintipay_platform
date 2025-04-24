package cv.ntinnovation.vintipay.adapter.in.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para testar o webhook do Stripe sem autenticação
 * Este controlador é apenas para testes e deve ser removido em produção
 */
@Slf4j
@RestController
@RequestMapping("/test/webhook/stripe")
public class StripeWebhookTestController {

    @PostMapping
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "Stripe-Signature", required = false) String sigHeader) {
        
        log.info("Webhook de teste recebido!");
        log.info("Payload: {}", payload);
        log.info("Signature: {}", sigHeader);
        
        return ResponseEntity.ok("Webhook recebido com sucesso!");
    }
}
