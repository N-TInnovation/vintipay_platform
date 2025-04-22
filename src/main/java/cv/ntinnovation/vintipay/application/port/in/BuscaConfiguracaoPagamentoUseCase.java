package cv.ntinnovation.vintipay.application.port.in;

import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoPagamento;

import java.util.List;

public interface BuscaConfiguracaoPagamentoUseCase {
    ConfiguracaoPagamento buscarPorId(Long id);
    ConfiguracaoPagamento buscarPorPosId(String posId);
    List<ConfiguracaoPagamento> buscarTodas();
}
