package cv.ntinnovation.vintipay.application.core.domain.enums;

/**
 * Enum que representa os possíveis estados de uma intenção de pagamento
 */
public enum StatusIntencaoPagamentoEnum {
    /**
     * Pagamento pendente, ainda não foi processado
     */
    PENDENTE,
    
    /**
     * Pagamento foi processado com sucesso
     */
    PAGO,
    
    /**
     * Pagamento foi cancelado pelo usuário ou pelo sistema
     */
    CANCELADO,
    
    /**
     * Pagamento falhou durante o processamento
     */
    FALHA,
    
    /**
     * Pagamento expirou (tempo limite excedido)
     */
    EXPIRADO,
    
    /**
     * Pagamento está sendo processado
     */
    PROCESSANDO,
    
    /**
     * Pagamento foi reembolsado
     */
    REEMBOLSADO
}
