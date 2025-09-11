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
    REEMBOLSADO;
    
    /**
     * Converte um StatusPagamentoEnum para StatusIntencaoPagamentoEnum
     * 
     * @param statusPagamento o status de pagamento a ser convertido
     * @return o status de intenção de pagamento equivalente
     */
    public static StatusIntencaoPagamentoEnum fromStatusPagamento(StatusPagamentoEnum statusPagamento) {
        if (statusPagamento == null) {
            return null;
        }
        
        switch (statusPagamento) {
            case PENDENTE:
                return PENDENTE;
            case PAGO:
                return PAGO;
            case CANCELADO:
                return CANCELADO;
            case PROCESSANDO:
                return PROCESSANDO;
            case ESTORNADO:
                return REEMBOLSADO;
            case ERRO:
                return FALHA;
            default:
                throw new IllegalArgumentException("Status de pagamento não suportado: " + statusPagamento);
        }
    }
    
    /**
     * Converte este StatusIntencaoPagamentoEnum para um StatusPagamentoEnum
     * 
     * @return o status de pagamento equivalente
     */
    public StatusPagamentoEnum toStatusPagamento() {
        switch (this) {
            case PENDENTE:
                return StatusPagamentoEnum.PENDENTE;
            case PAGO:
                return StatusPagamentoEnum.PAGO;
            case CANCELADO:
                return StatusPagamentoEnum.CANCELADO;
            case PROCESSANDO:
                return StatusPagamentoEnum.PROCESSANDO;
            case FALHA:
                return StatusPagamentoEnum.ERRO;
            case EXPIRADO:
                return StatusPagamentoEnum.CANCELADO; // Mapeando expirado para cancelado
            case REEMBOLSADO:
                return StatusPagamentoEnum.CANCELADO; // Mapeando reembolsado para cancelado
            default:
                throw new IllegalArgumentException("Status de intenção de pagamento não suportado: " + this);
        }
    }
}
