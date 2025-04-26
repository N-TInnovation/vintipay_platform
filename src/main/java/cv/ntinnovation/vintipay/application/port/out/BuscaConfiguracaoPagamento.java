package cv.ntinnovation.vintipay.application.port.out;

import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoPagamento;

import java.util.List;
import java.util.Optional;

/**
 * Interface para busca de configurações de pagamento
 */
public interface BuscaConfiguracaoPagamento {
    
    /**
     * Busca uma configuração de pagamento pelo ID
     * 
     * @param id ID da configuração de pagamento
     * @return Configuração de pagamento, se encontrada
     */
    Optional<ConfiguracaoPagamento> buscarPorId(Long id);
    
    /**
     * Busca todas as configurações de pagamento de um usuário
     * 
     * @param usuarioId ID do usuário
     * @return Lista de configurações de pagamento do usuário
     */
    List<ConfiguracaoPagamento> buscarPorUsuarioId(Long usuarioId);
    
    /**
     * Busca todas as configurações de pagamento ativas de um usuário
     * 
     * @param usuarioId ID do usuário
     * @return Lista de configurações de pagamento ativas do usuário
     */
    List<ConfiguracaoPagamento> buscarAtivasPorUsuarioId(Long usuarioId);
    
    /**
     * Verifica se uma configuração de pagamento pertence a um usuário
     * 
     * @param id ID da configuração de pagamento
     * @param usuarioId ID do usuário
     * @return true se a configuração pertence ao usuário, false caso contrário
     */
    boolean existePorIdEUsuarioId(Long id, Long usuarioId);
}
