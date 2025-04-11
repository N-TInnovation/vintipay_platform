package cv.ntinnovation.gateway_payment.application.port.out;

import cv.ntinnovation.gateway_payment.application.core.domain.entity.MetodoPagamento;

public interface SalvaMetodoPagamentoPort {
    MetodoPagamento salvar(MetodoPagamento metodoPagamento);
}
