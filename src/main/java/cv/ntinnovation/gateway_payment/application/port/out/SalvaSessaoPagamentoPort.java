package cv.ntinnovation.gateway_payment.application.port.out;

import cv.ntinnovation.gateway_payment.application.core.domain.entity.SessaoPagamento;

public interface SalvaSessaoPagamentoPort {
    SessaoPagamento salvar(SessaoPagamento sessaoPagamento);
}
