package cv.ntinnovation.vintipay.application.core.usecase;

import cv.ntinnovation.vintipay.application.core.domain.entity.Pagamento;
import cv.ntinnovation.vintipay.application.core.domain.enums.WebhookEventoEnum;
import cv.ntinnovation.vintipay.application.port.in.WebhookUseCase;
import cv.ntinnovation.vintipay.application.port.out.WebhookKafkaServicePort;
import cv.ntinnovation.vintipay.application.port.out.WebhookServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class WebhookUseCaseImpl implements WebhookUseCase {
    private final WebhookKafkaServicePort webhookKafkaServicePort;
    private final WebhookServicePort webhookServicePort;

    @Override
    public void receberPagamentoWebhook(String url, Pagamento payload) {
        webhookServicePort.enviarWebhook(url, WebhookEventoEnum.PAGAMENTO, payload);
    }

    @Override
    public void enviarPagamentoWebhook(String url, Pagamento payload) {
        webhookKafkaServicePort.enviarWebhook(url, WebhookEventoEnum.PAGAMENTO, payload);
    }
}
