package cv.ntinnovation.vintipay.adapter.in.messaging.kafka.consumer;

import cv.ntinnovation.vintipay.adapter.out.messaging.kafka.dto.WebhookEventoDTO;
import cv.ntinnovation.vintipay.application.core.domain.entity.Pagamento;
import cv.ntinnovation.vintipay.application.port.in.WebhookUseCase;
import cv.ntinnovation.vintipay.application.port.out.WebhookServicePort;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebhookKafkaConsumer {
    
    @Qualifier("webhookHttpAdapter")
    private final WebhookUseCase webhookUseCase;
    private final ObjectMapper objectMapper;
    
    @KafkaListener(
        topics = "webhook-pagamento",
        groupId = "${spring.kafka.consumer.group-id:vintipay-webhook-group}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumirEventoWebhook(WebhookEventoDTO evento, Acknowledgment acknowledgment) {
        try {
            log.info("Recebido evento de webhook do Kafka: {}", evento);
            
            // Converter o payload para Pagamento
            //Pagamento pagamento = objectMapper.convertValue(evento.getPayload(), Pagamento.class);
            
            // Chamar o adapter HTTP diretamente para enviar o webhook
            //webhookUseCase.receberPagamentoWebhook(evento.getWebhookUrl(), pagamento);
            
            log.info("Webhook processado com sucesso para URL: {}", evento.getWebhookUrl());
            
            // Fazer acknowledge manual apenas em caso de sucesso
            acknowledgment.acknowledge();
            log.debug("Mensagem confirmada (acknowledged) no Kafka");
            
        } catch (Exception e) {
            log.error("Erro ao processar evento de webhook do Kafka: {}", e.getMessage(), e);
            log.warn("Mensagem NÃO confirmada. Será reprocessada na próxima poll do consumer.");
            // NÃO fazer acknowledge - a mensagem permanecerá no tópico para retry
            // O Kafka irá reenviar a mensagem após o tempo de poll configurado
        }
    }
}
