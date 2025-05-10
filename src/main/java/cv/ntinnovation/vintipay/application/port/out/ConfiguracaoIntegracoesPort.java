package cv.ntinnovation.vintipay.application.port.out;

import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoIntegracoes;

import java.util.List;
import java.util.Optional;

public interface ConfiguracaoIntegracoesPort {
    ConfiguracaoIntegracoes salvar(ConfiguracaoIntegracoes configuracaoIntegracoes);
    List<ConfiguracaoIntegracoes> buscarTodas(Long usuarioId);
    List<ConfiguracaoIntegracoes> buscarTodasPorConfiguracaoPagamento(Long id);
    /**
     * Busca todas as integrações ativas associadas às configurações ativas do usuário
     */
    List<ConfiguracaoIntegracoes> buscarAtivasPorUsuario(Long usuarioId);

    ConfiguracaoIntegracoes buscarPorId(Long id);

    Optional<ConfiguracaoIntegracoes> buscarPorIdUsuario(Long id, Long usuarioId);

    boolean deletarPorId(Long id);

    boolean deletarPorIdUsuario(Long id, Long usuarioId);

    boolean existeTipoIntegracaoEmConfiguracaoPagamentoPorUsuario(Long id, String tipoIntegracao, Long usuarioId);


}
