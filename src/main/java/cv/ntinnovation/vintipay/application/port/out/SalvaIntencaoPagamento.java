package cv.ntinnovation.vintipay.application.port.out;

import cv.ntinnovation.vintipay.application.core.domain.entity.IntencaoPagamento;

public interface SalvaIntencaoPagamento {
    IntencaoPagamento salvar(IntencaoPagamento intencaoPagamento);
}
