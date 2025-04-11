package cv.ntinnovation.gateway_payment.application.port.out;

import cv.ntinnovation.gateway_payment.application.core.domain.entity.ConfiguracaoPagamento;

import java.util.Optional;

public interface BuscaConfiguracaoPagamentoPort {
    Optional<ConfiguracaoPagamento> buscarPorCredenciais(String apiKey, String apiSecret);
    Optional<ConfiguracaoPagamento> buscarPorApiKey(String apiKey);
    Optional<ConfiguracaoPagamento> buscarPorPosId(String posId);
}
