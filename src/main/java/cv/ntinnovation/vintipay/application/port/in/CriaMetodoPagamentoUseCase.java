package cv.ntinnovation.vintipay.application.port.in;

import cv.ntinnovation.vintipay.application.core.domain.entity.MetodoPagamento;

public interface CriaMetodoPagamentoUseCase {
    MetodoPagamento criar(MetodoPagamento metodoPagamento);
}
