package cv.ntinnovation.vintipay.application.port.in;

import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoIntegracoes;

public interface AtualizaIntegracoesPagamentoUseCase {
    ConfiguracaoIntegracoes atualizar(ConfiguracaoIntegracoes configuracaoIntegracoes);
    void ativar(Long id);
    void desativar(Long id);

    boolean deletarPorIdUsuario(Long id, Long usuarioId);
}
