package cv.ntinnovation.gateway_payment.application.port.in;

import cv.ntinnovation.gateway_payment.application.core.domain.entity.ConfiguracaoPagamento;

public interface BuscaConfiguracaoPagamentoAtivaUseCase {
    ConfiguracaoPagamento buscarPorCredenciais(String clientId, String clientSecret);
    ConfiguracaoPagamento buscarPorClientId(String clientId);
}
