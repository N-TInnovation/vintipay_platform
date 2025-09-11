package cv.ntinnovation.vintipay.application.port.in;

import cv.ntinnovation.vintipay.application.core.domain.entity.Pagamento;
import cv.ntinnovation.vintipay.application.core.domain.enums.StatusPagamentoEnum;

public interface AtualizaStatusPagamentoUseCase {
    Pagamento atualizar(String merchantRef, StatusPagamentoEnum status, String message, String fingerPrint, String pan, String clearingPeriod, String transactionId);
}
