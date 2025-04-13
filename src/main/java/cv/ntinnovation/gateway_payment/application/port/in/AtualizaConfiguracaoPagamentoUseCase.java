package cv.ntinnovation.gateway_payment.application.port.in;

import cv.ntinnovation.gateway_payment.application.core.domain.entity.ConfiguracaoPagamento;

public interface AtualizaConfiguracaoPagamentoUseCase {
    ConfiguracaoPagamento atualizar(ConfiguracaoPagamento configuracao);
    void ativar(Long id);
    void desativar(Long id);
}
