package cv.ntinnovation.vintipay.application.port.out;

import cv.ntinnovation.vintipay.application.core.domain.entity.IntencaoPagamento;

import java.util.Optional;

/**
 * Interface para busca de intenções de pagamento
 */
public interface BuscaIntencaoPagamento {
    
    /**
     * Busca uma intenção de pagamento pelo ID
     * 
     * @param id ID da intenção de pagamento
     * @return Optional contendo a intenção, se encontrada
     */
    IntencaoPagamento buscarPorId(String id);
    
    /**
     * Busca uma intenção de pagamento pela referência
     * 
     * @param referencia Referência da intenção de pagamento
     * @return Optional contendo a intenção, se encontrada
     */
    Optional<IntencaoPagamento> buscarPorReferencia(String referencia);
}
