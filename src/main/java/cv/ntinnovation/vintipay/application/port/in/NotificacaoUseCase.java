package cv.ntinnovation.vintipay.application.port.in;

/**
 * Interface de caso de uso para notificações
 * Responsável por gerenciar o envio de diferentes tipos de notificações
 */
public interface NotificacaoUseCase {
    
    /**
     * Envia um email para o destinatário especificado
     * 
     * @param destinatario Email do destinatário
     * @param toName Nome do destinatário
     * @param assunto Assunto do email
     * @param conteudo Conteúdo HTML do email
     * @return true se o email foi enviado com sucesso, false caso contrário
     */
    boolean enviarEmail(String destinatario, String toName, String assunto, String conteudo);
    
    /**
     * Envia um email de confirmação de pagamento
     * 
     * @param destinatario Email do destinatário
     * @param destinatarioNome Nome do destinatário
     * @param referenciaPagamento Referência do pagamento
     * @param valor Valor da transação
     * @return true se o email foi enviado com sucesso, false caso contrário
     */
    boolean enviarEmailPagamento(String destinatario, String destinatarioNome, String referenciaPagamento, Double valor);

    /**
     * Envia um email de notificação de pagamento recebido para o comerciante
     * 
     * @param emailComerciante Email do comerciante/lojista
     * @param nomeComerciante Nome do comerciante/lojista
     * @param nomeCliente Nome do cliente que realizou o pagamento
     * @param emailCliente Email do cliente que realizou o pagamento
     * @param referenciaPagamento Referência do pagamento
     * @param valor Valor da transação
     * @return true se o email foi enviado com sucesso, false caso contrário
     */
    boolean notificarComerciante(String emailComerciante, String nomeComerciante, String nomeCliente, 
                                 String emailCliente, String referenciaPagamento, Double valor);
}
