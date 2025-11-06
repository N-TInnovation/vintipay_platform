package cv.ntinnovation.vintipay.application.port.in;

import cv.ntinnovation.vintipay.application.core.domain.entity.Pagamento;

public interface WebhookUseCase {
    void enviarWebhook(String url, Pagamento payload);
}
