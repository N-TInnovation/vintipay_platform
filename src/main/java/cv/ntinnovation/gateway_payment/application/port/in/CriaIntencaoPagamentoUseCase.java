package cv.ntinnovation.gateway_payment.application.port.in;

import cv.ntinnovation.gateway_payment.application.core.domain.entity.IntencaoPagamento;

public interface CriaIntencaoPagamentoUseCase {
    IntencaoPagamento criar(IntencaoPagamento intencaoPagamento);
}
