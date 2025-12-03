package cv.ntinnovation.vintipay.adapter.out.messaging.kafka.adapter;

import cv.ntinnovation.vintipay.adapter.out.messaging.kafka.dto.WebhookEventoDTO;
import cv.ntinnovation.vintipay.adapter.out.messaging.kafka.producer.WebhookKafkaProducer;
import cv.ntinnovation.vintipay.application.core.domain.entity.Pagamento;
import cv.ntinnovation.vintipay.application.core.domain.enums.WebhookEventoEnum;
import cv.ntinnovation.vintipay.application.port.out.WebhookKafkaServicePort;
import cv.ntinnovation.vintipay.application.port.out.WebhookServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Adapter que publica eventos de webhook no Kafka.
 * Marcado como @Primary para ser usado preferencialmente pelo WebhookUseCase.
 */
@Component
@Primary
@RequiredArgsConstructor
@Slf4j
public class WebhookKafkaAdapter implements WebhookKafkaServicePort {
    
    private final WebhookKafkaProducer webhookKafkaProducer;
    
    @Override
    public void enviarWebhook(String url, WebhookEventoEnum tipo, Pagamento payload) {
        log.info("Publicando evento de webhook no Kafka para URL: {}", url);
        
        WebhookEventoDTO evento = WebhookEventoDTO.builder()
                .webhookUrl(url)
                .tipo(tipo.name())
                .payload(payload)
                .timestamp(LocalDateTime.now().toString())
                .build();
        
        webhookKafkaProducer.enviarEventoWebhook(evento);
    }
}
