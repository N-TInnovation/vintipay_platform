package cv.ntinnovation.vintipay.application.port.in;

import cv.ntinnovation.vintipay.application.core.domain.entity.Pagamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Caso de uso para buscar histórico de pagamentos do usuário
 */
public interface BuscaHistoricoPagamentoUseCase {
    
    /**
     * Busca o histórico de pagamentos de um usuário
     * @param usuarioId ID do usuário
     * @param status Status do pagamento (opcional)
     * @param pageable Informações de paginação
     * @return Página com os pagamentos do usuário
     */
    Page<Pagamento> buscarHistoricoPagamentos(Long usuarioId, String status, Pageable pageable);
}
