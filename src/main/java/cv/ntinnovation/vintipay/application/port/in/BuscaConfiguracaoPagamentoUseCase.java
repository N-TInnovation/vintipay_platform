package cv.ntinnovation.vintipay.application.port.in;

import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoPagamento;

import java.util.List;
import java.util.Optional;

public interface BuscaConfiguracaoPagamentoUseCase {
    ConfiguracaoPagamento buscarPorId(Long id);
    ConfiguracaoPagamento buscarPorPosId(String posId);
    List<ConfiguracaoPagamento> buscarTodas();
    Optional<ConfiguracaoPagamento> buscarPorUsuarioId(Long usuarioId);
    List<ConfiguracaoPagamento> buscarPorUsuarioIdLista(Long usuarioId);
}
