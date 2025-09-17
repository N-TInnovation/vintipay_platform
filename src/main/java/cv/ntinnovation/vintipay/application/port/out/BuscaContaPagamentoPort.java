package cv.ntinnovation.vintipay.application.port.out;

import cv.ntinnovation.vintipay.application.core.domain.entity.ContaPagamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Porta de saída para busca de contas de pagamento (clientes)
 */
public interface BuscaContaPagamentoPort {
    
    /**
     * Busca todas as contas de pagamento com paginação
     * 
     * @param pageable Configuração de paginação
     * @return Página com as contas de pagamento
     */
    Page<ContaPagamento> buscarTodos(Pageable pageable);
    
    /**
     * Busca uma conta de pagamento por ID
     * 
     * @param id ID da conta de pagamento
     * @return Optional com a conta de pagamento, se encontrada
     */
    Optional<ContaPagamento> buscarPorId(Long id);
    
    /**
     * Busca uma conta de pagamento por email
     * 
     * @param email Email da conta de pagamento
     * @return Optional com a conta de pagamento, se encontrada
     */
    Optional<ContaPagamento> buscarPorEmail(String email);
    
    /**
     * Busca contas de pagamento por nome ou email contendo o texto da query
     * 
     * @param query Texto a ser buscado no nome ou email
     * @param pageable Configuração de paginação
     * @return Página com os resultados da busca
     */
    Page<ContaPagamento> buscarPorNomeOuEmail(String query, Pageable pageable);
    
    /**
     * Conta o número total de contas de pagamento
     * 
     * @return Número total de contas de pagamento
     */
    long contarTodos();
}
