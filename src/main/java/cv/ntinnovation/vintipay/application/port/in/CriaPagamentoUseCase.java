package cv.ntinnovation.vintipay.application.port.in;

import cv.ntinnovation.vintipay.application.core.domain.entity.Pagamento;

public interface CriaPagamentoUseCase {
    Pagamento criar(String intencaoPagamentoRef);
}
