package cv.ntinnovation.gateway_payment.application.port.in;

import cv.ntinnovation.gateway_payment.application.core.domain.entity.Pagamento;

public interface CriaPagamentoUseCase {
    Pagamento criar(String intencaoPagamentoRef);
}
