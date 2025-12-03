package cv.ntinnovation.vintipay.adapter.out.client.api.vintipay;

import cv.ntinnovation.vintipay.adapter.out.client.api.vintipay.request.EventoRequest;
import cv.ntinnovation.vintipay.application.core.domain.entity.Pagamento;
import cv.ntinnovation.vintipay.application.core.domain.enums.WebhookEventoEnum;
import cv.ntinnovation.vintipay.application.port.out.WebhookServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;

/**
 * Adapter que faz a chamada HTTP real para o webhook.
 * É usado pelo Consumer do Kafka para enviar os eventos.
 */
@Component("webhookHttpAdapter")
@RequiredArgsConstructor
@Slf4j
public class WebhookAdapter implements WebhookServicePort {
    
    private final Vintipay vintipay;
    
    @Override
    public void enviarWebhook(String url, WebhookEventoEnum tipo, Pagamento payload) {
        try {
            log.info("Enviando webhook HTTP para URL: {}", url);
            
            EventoRequest<Pagamento> eventoRequest = EventoRequest.<Pagamento>builder()
                    .tipo(tipo.name())
                    .data(payload)
                    .build();
            
            vintipay.enviaEventosWebhook(URI.create(url), "Bearer ", eventoRequest);
            
            log.info("Webhook HTTP enviado com sucesso para URL: {}", url);
        } catch (Exception e) {
            log.error("Erro ao enviar webhook HTTP para URL {}: {}", url, e.getMessage(), e);
            throw new RuntimeException("Falha ao enviar webhook HTTP", e);
        }
    }
}
