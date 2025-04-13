package cv.ntinnovation.gateway_payment.application.port.in;

import cv.ntinnovation.gateway_payment.application.core.domain.entity.ConfiguracaoPagamento;

public interface CriaConfiguracaoPagamentoUseCase {
    ConfiguracaoPagamento criar(ConfiguracaoPagamento configuracao);
}
