package cv.ntinnovation.gateway_payment.application.core.domain.exception;

public class PagamentoStatusNaoPendenteException extends RuntimeException {
    public PagamentoStatusNaoPendenteException(String message) {
        super(message);
    }
}
