package cv.ntinnovation.vintipay.application.port.in;

import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoPagamento;

public interface AtualizaConfiguracaoPagamentoUseCase {
    ConfiguracaoPagamento atualizar(ConfiguracaoPagamento configuracao);
    void ativar(Long id);
    void desativar(Long id);
}
