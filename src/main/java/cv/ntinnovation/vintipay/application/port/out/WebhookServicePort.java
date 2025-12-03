package cv.ntinnovation.vintipay.application.port.out;

import cv.ntinnovation.vintipay.application.core.domain.entity.Pagamento;
import cv.ntinnovation.vintipay.application.core.domain.enums.WebhookEventoEnum;

public interface WebhookServicePort {
    void enviarWebhook(String url, WebhookEventoEnum tipo, Pagamento payload);
}
