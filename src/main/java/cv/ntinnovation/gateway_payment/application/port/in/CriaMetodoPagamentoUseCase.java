package cv.ntinnovation.gateway_payment.application.port.in;

import cv.ntinnovation.gateway_payment.application.core.domain.entity.MetodoPagamento;

public interface CriaMetodoPagamentoUseCase {
    MetodoPagamento criar(MetodoPagamento metodoPagamento);
}
