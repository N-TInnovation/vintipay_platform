package cv.ntinnovation.vintipay.application.port.out;

import cv.ntinnovation.vintipay.application.core.domain.entity.PreferenciasNotificacao;

/**
 * Porta para gerenciamento de preferências de notificação
 */
public interface GerenciaPreferenciasNotificacaoPort {

    /**
     * Busca as preferências de notificação por ID do usuário
     * 
     * @param usuarioId ID do usuário
     * @return Preferências de notificação encontradas ou preferências padrão se não existir
     */
    PreferenciasNotificacao buscarPorUsuario(Long usuarioId);
    
    /**
     * Salva ou atualiza as preferências de notificação
     * 
     * @param preferenciasNotificacao Preferências de notificação para salvar
     * @return Preferências de notificação salvas
     */
    PreferenciasNotificacao salvar(PreferenciasNotificacao preferenciasNotificacao);
}
