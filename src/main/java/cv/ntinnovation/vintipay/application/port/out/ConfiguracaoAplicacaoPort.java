package cv.ntinnovation.vintipay.application.port.out;

import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoAplicacao;

import java.util.List;

public interface ConfiguracaoAplicacaoPort {
    void salvar(ConfiguracaoAplicacao configuracaoAplicacao);
    List<ConfiguracaoAplicacao> buscarTodas(Long configuracaoPagamentoId, Long usuarioId);
}
