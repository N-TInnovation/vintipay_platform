package cv.ntinnovation.gateway_payment.application.port.out;

import cv.ntinnovation.gateway_payment.application.core.domain.entity.ConfiguracaoPagamento;

public interface SalvaConfiguracaoPagamentoPort {
    ConfiguracaoPagamento salvar(ConfiguracaoPagamento configuracaoPagamento);
}
