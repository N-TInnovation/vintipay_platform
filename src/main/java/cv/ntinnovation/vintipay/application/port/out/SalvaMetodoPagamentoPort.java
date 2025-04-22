package cv.ntinnovation.vintipay.application.port.out;

import cv.ntinnovation.vintipay.application.core.domain.entity.MetodoPagamento;

public interface SalvaMetodoPagamentoPort {
    MetodoPagamento salvar(MetodoPagamento metodoPagamento);
}
