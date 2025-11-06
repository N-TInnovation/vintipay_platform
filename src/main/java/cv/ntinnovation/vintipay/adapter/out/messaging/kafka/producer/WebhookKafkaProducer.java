package cv.ntinnovation.vintipay.adapter.out.messaging.kafka.producer;

import cv.ntinnovation.vintipay.adapter.out.messaging.kafka.dto.WebhookEventoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebhookKafkaProducer {
    
    private static final String TOPIC_WEBHOOK_PAGAMENTO = "webhook-pagamento";
    
    private final KafkaTemplate<String, WebhookEventoDTO> kafkaTemplate;
    
    public void enviarEventoWebhook(WebhookEventoDTO evento) {
        try {
            log.info("Enviando evento de webhook para o tópico {}: {}", TOPIC_WEBHOOK_PAGAMENTO, evento);
            kafkaTemplate.send(TOPIC_WEBHOOK_PAGAMENTO, evento.getWebhookUrl(), evento);
            log.info("Evento de webhook enviado com sucesso para URL: {}", evento.getWebhookUrl());
        } catch (Exception e) {
            log.error("Erro ao enviar evento de webhook para o Kafka: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao enviar evento de webhook para o Kafka", e);
        }
    }
}
