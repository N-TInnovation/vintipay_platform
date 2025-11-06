package cv.ntinnovation.vintipay.application.core.usecase;

import cv.ntinnovation.vintipay.application.core.domain.entity.Pagamento;
import cv.ntinnovation.vintipay.application.port.in.WebhookUseCase;
import cv.ntinnovation.vintipay.application.port.out.WebhookServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class WebhookUseCaseImpl implements WebhookUseCase {
    private final WebhookServicePort webhookServicePort;
    @Override
    public void enviarWebhook(String url, Pagamento payload) {
        webhookServicePort.enviarWebhook(url, payload);
    }
}
