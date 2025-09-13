package cv.ntinnovation.vintipay.application.core.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum StatusPagamentoEnum {

    PENDENTE("PENDENTE", "retorno/pendente"),
    PROCESSANDO("PROCESSANDO", "retorno/pendente"),
    PAGO("PAGO", "retorno/sucesso"),
    ERRO("ERRO", "retorno/erro"),
    ESTORNADO("ESTORNADO", "retorno/estorno"),
    EXPIRADO("EXPIRADO", "retorno/expirado"),
    CANCELADO("CANCELADO", "retorno/cancelado");

    private final String status;
    private final String retorno;
}
