package cv.ntinnovation.gateway_payment.application.core.domain.exception;

public class IntencaoPagamentoNaoExisteExeption extends RuntimeException{
    public IntencaoPagamentoNaoExisteExeption(String message) {
        super(message);
    }
}
