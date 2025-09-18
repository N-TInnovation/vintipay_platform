package cv.ntinnovation.vintipay.application.port.in;

import cv.ntinnovation.vintipay.application.core.domain.entity.IntencaoPagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.SubscricaoPagamento;

public interface CriaSubscricaoPagamentoUseCase {
    SubscricaoPagamento subscrever(IntencaoPagamento intencaoPagamento);
}
