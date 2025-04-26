package cv.ntinnovation.vintipay.application.port.out;

import cv.ntinnovation.vintipay.application.core.domain.entity.ContaPagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.SessaoPagamento;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Interface para processamento de pagamentos com o provedor de pagamentos
 */
public interface ProcessadorPagamento {
    
    /**
     * Cria uma sessão de pagamento no provedor
     * 
     * @param referencia Referência única da transação
     * @param nome Nome do produto/serviço
     * @param descricao Descrição do produto/serviço
     * @param valor Valor a ser cobrado
     * @param moeda Moeda do pagamento (ex: CVE)
     * @param urlRetornoSucesso URL para redirecionamento em caso de sucesso
     * @param urlRetornoCancelamento URL para redirecionamento em caso de cancelamento
     * @return Sessão de pagamento criada
     */
    SessaoPagamento criarSessaoPagamento(String referencia, String nome, String descricao, 
                                        BigDecimal valor, String moeda,
                                        String urlRetornoSucesso, String urlRetornoCancelamento);
    
    /**
     * Cria uma sessão de pagamento no provedor com dados do comprador
     * 
     * @param referencia Referência única da transação
     * @param nome Nome do produto/serviço
     * @param descricao Descrição do produto/serviço
     * @param valor Valor a ser cobrado
     * @param moeda Moeda do pagamento (ex: CVE)
     * @param urlRetornoSucesso URL para redirecionamento em caso de sucesso
     * @param urlRetornoCancelamento URL para redirecionamento em caso de cancelamento
     * @param contaPagamento Dados da conta de pagamento do comprador
     * @param merchantSession ID da sessão do comerciante
     * @return Sessão de pagamento criada
     */
    default SessaoPagamento criarSessaoPagamento(String referencia, String nome, String descricao, 
                                        BigDecimal valor, String moeda,
                                        String urlRetornoSucesso, String urlRetornoCancelamento,
                                        ContaPagamento contaPagamento, String merchantSession) {
        // Implementação padrão que ignora os dados do comprador
        return criarSessaoPagamento(referencia, nome, descricao, valor, moeda, 
                                   urlRetornoSucesso, urlRetornoCancelamento);
    }
    
    /**
     * Busca uma sessão de pagamento pelo ID
     * 
     * @param sessionId ID da sessão de pagamento
     * @return Optional contendo a sessão, se encontrada
     */
    Optional<SessaoPagamento> buscarSessaoPorId(String sessionId);
    
    /**
     * Verifica o status de uma sessão de pagamento
     * 
     * @param sessionId ID da sessão de pagamento
     * @return true se o pagamento foi bem-sucedido
     */
    boolean verificarStatusPagamento(String sessionId);
    
    /**
     * Cancela uma sessão de pagamento
     * 
     * @param sessionId ID da sessão de pagamento
     * @return true se o cancelamento foi bem-sucedido
     */
    boolean cancelarSessaoPagamento(String sessionId);
}
