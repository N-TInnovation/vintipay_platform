package cv.ntinnovation.gateway_payment.application.port.in;

import cv.ntinnovation.gateway_payment.application.core.domain.entity.Pagamento;
import cv.ntinnovation.gateway_payment.application.core.domain.enums.StatusPagamentoEnum;

public interface AtualizaStatusPagamentoUseCase {
    Pagamento atualizar(String merchantRef, StatusPagamentoEnum status, String message, String fingerPrint, String pan);
}
