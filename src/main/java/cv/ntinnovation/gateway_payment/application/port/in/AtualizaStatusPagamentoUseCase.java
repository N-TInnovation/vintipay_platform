package cv.ntinnovation.gateway_payment.application.port.in;

public interface AtualizaStatusPagamentoUseCase {
    void atualizar(String merchantRef, String status, String message, String authorizationCode, String pan);
}
