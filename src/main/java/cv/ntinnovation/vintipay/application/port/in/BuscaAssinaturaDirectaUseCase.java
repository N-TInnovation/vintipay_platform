package cv.ntinnovation.vintipay.application.port.in;

import cv.ntinnovation.vintipay.application.core.domain.entity.PlanoAssinatura;
import java.util.Optional;

/**
 * Caso de uso para buscar assinaturas diretamente
 */
public interface BuscaAssinaturaDirectaUseCase {
    
    /**
     * Busca uma assinatura pelo ID do usuário
     * @param usuarioId ID do usuário
     * @return Optional contendo a assinatura se encontrada
     */
    Optional<PlanoAssinatura> buscarAssinaturaPorUsuarioId(Long usuarioId);
}
