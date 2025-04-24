package cv.ntinnovation.vintipay.application.port.out;

/**
 * Interface para consultas relacionadas a configurações de pagamento no repositório
 */
public interface ConsultaConfiguracaoPagamentoPort {
    
    /**
     * Verifica se existe uma configuração com o POS ID fornecido
     * 
     * @param posId O POS ID a ser verificado
     * @return true se existir uma configuração com este POS ID, false caso contrário
     */
    boolean existePorPosId(String posId);
    
    /**
     * Verifica se existe uma configuração com o POS ID fornecido para um usuário específico
     * 
     * @param posId O POS ID a ser verificado
     * @param usuarioId O ID do usuário
     * @return true se existir uma configuração com este POS ID para o usuário, false caso contrário
     */
    boolean existePorPosIdEUsuario(String posId, Long usuarioId);
    
    /**
     * Verifica se existe uma configuração com o POS ID fornecido para outros usuários
     * 
     * @param posId O POS ID a ser verificado
     * @param usuarioId O ID do usuário a ser excluído da verificação
     * @return true se existir uma configuração com este POS ID para outros usuários, false caso contrário
     */
    boolean existePorPosIdEOutrosUsuarios(String posId, Long usuarioId);
}
