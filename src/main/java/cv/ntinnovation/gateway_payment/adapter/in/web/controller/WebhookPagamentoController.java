package cv.ntinnovation.gateway_payment.adapter.in.web.controller;

import cv.ntinnovation.gateway_payment.adapter.in.web.dto.SispWebhookRequest;
import cv.ntinnovation.gateway_payment.application.port.in.AtualizaStatusPagamentoUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class WebhookPagamentoController {

    private final AtualizaStatusPagamentoUseCase atualizaStatusPagamentoUseCase;

    @PostMapping("/sisp/pagamentos/retorno/{reference}")
    public ResponseEntity<Void> handlePaymentWebhook(@PathVariable String reference, @RequestBody SispWebhookRequest request) {
        log.info("Recebido webhook do SISP: {}", request);
        
        try {
            atualizaStatusPagamentoUseCase.atualizar(
                request.getMerchantRef(),
                request.getStatus(),
                request.getMessage(),
                request.getAuthorizationCode(),
                request.getPan()
            );
            
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Erro ao processar webhook do SISP", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
