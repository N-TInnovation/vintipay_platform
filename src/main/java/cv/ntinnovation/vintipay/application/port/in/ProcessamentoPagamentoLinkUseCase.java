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
     * Cria uma sessão de pagamento para um link com dados do comprador
     * 
     * @param codigoLink Código do link de pagamento
     * @param successUrl URL para redirecionamento em caso de sucesso
     * @param cancelUrl URL para redirecionamento em caso de cancelamento
     * @param email Email do comprador
     * @param cidade Cidade do comprador
     * @param pais País do comprador
     * @param endereco Endereço do comprador
     * @param codigoPostal Código postal do comprador
     * @param merchantSession ID da sessão do comerciante
     * @param codigoTelefone Código do telefone do comprador
     * @param numeroTelefone Número de telefone do comprador
     * @return URL da sessão de checkout
     */
    String criarSessaoPagamento(String codigoLink, String successUrl, String cancelUrl,
                               String email, String cidade, String pais, String endereco,
                               String codigoPostal, String merchantSession, String codigoTelefone,
                               String numeroTelefone);
    
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
