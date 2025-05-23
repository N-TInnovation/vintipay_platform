package cv.ntinnovation.vintipay.application.port.in;

import cv.ntinnovation.vintipay.application.core.domain.entity.PreferenciasNotificacao;

/**
 * Interface para o caso de uso de gerenciamento de preferências de notificação
 */
public interface GerenciaPreferenciasNotificacaoUseCase {

    /**
     * Busca as preferências de notificação de um usuário
     * 
     * @param usuarioId ID do usuário
     * @return Preferências de notificação do usuário
     */
    PreferenciasNotificacao buscarPreferenciasDoUsuario(Long usuarioId);
    
    /**
     * Atualiza as preferências de notificação de um usuário
     * 
     * @param preferenciasNotificacao Preferências de notificação a serem atualizadas
     * @return Preferências de notificação atualizadas
     */
    PreferenciasNotificacao atualizarPreferencias(PreferenciasNotificacao preferenciasNotificacao);
}
