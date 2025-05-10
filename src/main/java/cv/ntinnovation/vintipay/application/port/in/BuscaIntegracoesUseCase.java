package cv.ntinnovation.vintipay.application.port.in;

import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoIntegracoes;

import java.util.List;
import java.util.Optional;

/**
 * Interface para o caso de uso de busca de integrações
 */
public interface BuscaIntegracoesUseCase {
    
    /**
     * Busca todas as integrações ativas de um usuário
     * @param usuarioId ID do usuário
     * @return Lista de integrações ativas
     */
    List<ConfiguracaoIntegracoes> buscarIntegracoesAtivasPorUsuario(Long usuarioId);

    List<ConfiguracaoIntegracoes> buscarTodasIntegracoesPorIdUsuario(Long usuarioId);

    Optional<ConfiguracaoIntegracoes> buscarIntegracaoPorIdUsuario(Long id, Long usuarioId);

    boolean existeTipoIntegracaoEmConfiguracaoPagamentoPorUsuario(Long id, String tipoIntegracao, Long usuarioId);
}
