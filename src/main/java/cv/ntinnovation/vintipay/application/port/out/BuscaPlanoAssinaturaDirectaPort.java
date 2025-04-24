package cv.ntinnovation.vintipay.application.port.out;

import cv.ntinnovation.vintipay.application.core.domain.entity.PlanoAssinatura;
import java.util.Optional;

/**
 * Port para buscar assinaturas diretamente do repositório
 */
public interface BuscaPlanoAssinaturaDirectaPort {
    
    /**
     * Busca uma assinatura pelo ID do usuário
     * @param usuarioId ID do usuário
     * @return Optional contendo a assinatura se encontrada
     */
    Optional<PlanoAssinatura> buscarPorUsuarioId(Long usuarioId);
}
