package cv.ntinnovation.vintipay.application.port.in;

import cv.ntinnovation.vintipay.application.core.domain.entity.Pagamento;
import cv.ntinnovation.vintipay.application.core.domain.enums.WebhookEventoEnum;

public interface WebhookUseCase {

    void receberPagamentoWebhook(String url, Pagamento payload);
    void enviarPagamentoWebhook(String url, Pagamento payload);
}
