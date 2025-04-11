package cv.ntinnovation.gateway_payment.application.port.out;

import cv.ntinnovation.gateway_payment.application.core.domain.entity.IntencaoPagamento;

public interface SalvaIntencaoPagamento {
    IntencaoPagamento salvar(IntencaoPagamento intencaoPagamento);
}
