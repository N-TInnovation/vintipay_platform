package cv.ntinnovation.vintipay.application.port.in;

import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoPagamento;

public interface BuscaConfiguracaoPagamentoAtivaUseCase {
    ConfiguracaoPagamento buscarPorCredenciais(String clientId, String clientSecret);
    ConfiguracaoPagamento buscarPorClientId(String clientId);
}
