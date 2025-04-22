package cv.ntinnovation.vintipay.application.port.in;

import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoPagamento;

public interface CriaConfiguracaoPagamentoUseCase {
    ConfiguracaoPagamento criar(ConfiguracaoPagamento configuracao);
}
