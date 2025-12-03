package cv.ntinnovation.vintipay.application.core.domain.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum WebhookEventoEnum {
    PAGAMENTO,
    SUBSCRICAO,
    ESTORNO,
    CANCELAMENTO_SUBSCRICAO,
    COBRANCA_RECORRENTE,
    CANCELAMENTO_COBRANCA_RECORRENTE;
}
