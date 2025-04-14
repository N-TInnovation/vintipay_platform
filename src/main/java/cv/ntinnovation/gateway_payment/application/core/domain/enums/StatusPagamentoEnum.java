package cv.ntinnovation.gateway_payment.application.core.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum StatusPagamentoEnum {

    PENDENTE("PENDENTE"),
    PROCESSANDO("PROCESSANDO"),
    PAGO("PAGO"),
    ERRO("ERRO"),
    CANCELADO("CANCELADO");

    private final String status;
}
