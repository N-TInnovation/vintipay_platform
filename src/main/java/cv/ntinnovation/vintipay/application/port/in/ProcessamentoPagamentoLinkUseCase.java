package cv.ntinnovation.vintipay.application.port.in;

import cv.ntinnovation.vintipay.application.core.domain.entity.IntencaoPagamento;

import java.util.Optional;

public interface ProcessamentoPagamentoLinkUseCase {
    /**
     * Cria uma sessão de pagamento para um link
     * 
     * @param codigoLink Código do link de pagamento
     * @param successUrl URL para redirecionamento em caso de sucesso
     * @param cancelUrl URL para redirecionamento em caso de cancelamento
     * @return URL da sessão de checkout
     */
    String criarSessaoPagamento(String codigoLink, String successUrl, String cancelUrl);
    
    /**
     * Processa o pagamento bem-sucedido
     * 
     * @param sessionId ID da sessão de pagamento
     * @param codigoLink Código do link de pagamento
     * @return true se o processamento foi bem-sucedido
     */
    boolean processarPagamentoSucesso(String sessionId, String codigoLink);
    
    /**
     * Busca uma intenção de pagamento por código de link
     * 
     * @param codigoLink Código do link de pagamento
     * @return Optional contendo a intenção de pagamento, se encontrada
     */
    Optional<IntencaoPagamento> buscarIntencaoPorCodigoLink(String codigoLink);
}
