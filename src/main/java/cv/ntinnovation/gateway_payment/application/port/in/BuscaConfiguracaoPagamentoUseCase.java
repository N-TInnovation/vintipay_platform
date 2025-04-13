package cv.ntinnovation.gateway_payment.application.port.in;

import cv.ntinnovation.gateway_payment.application.core.domain.entity.ConfiguracaoPagamento;

import java.util.List;

public interface BuscaConfiguracaoPagamentoUseCase {
    ConfiguracaoPagamento buscarPorId(Long id);
    ConfiguracaoPagamento buscarPorPosId(String posId);
    List<ConfiguracaoPagamento> buscarTodas();
}
