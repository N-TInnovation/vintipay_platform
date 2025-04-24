package cv.ntinnovation.vintipay.application.port.in;

import cv.ntinnovation.vintipay.application.core.domain.entity.PlanoAssinatura;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;

import java.util.Optional;

public interface GerenciaAssinaturaUseCase {
    PlanoAssinatura criarAssinatura(Usuario usuario, Long planoId);
    
    /**
     * Cria uma assinatura sem integração com o Stripe (apenas para uso administrativo)
     * @param usuario Usuário para o qual a assinatura será criada
     * @param planoId ID do plano a ser assinado
     * @param skipStripeIntegration Se true, pula a integração com o Stripe
     * @return A assinatura criada
     */
    PlanoAssinatura criarAssinatura(Usuario usuario, Long planoId, boolean skipStripeIntegration);
    
    /**
     * Cria uma nova assinatura para o usuário
     * @param usuario Usuário para o qual a assinatura será criada
     * @param planoId ID do plano a ser assinado
     * @return true se a assinatura foi criada com sucesso, false caso contrário
     */
    boolean criarNovaAssinatura(Usuario usuario, Long planoId);
    
    Optional<PlanoAssinatura> buscarAssinaturaPorUsuario(Usuario usuario);
    
    /**
     * Busca uma assinatura pelo seu ID
     * @param id ID da assinatura a ser buscada
     * @return Optional contendo a assinatura se encontrada, ou vazio se não existir
     */
    Optional<PlanoAssinatura> buscarAssinaturaPorId(Long id);
    
    void cancelarAssinatura(Usuario usuario);
    
    /**
     * Cancela uma assinatura pelo seu ID
     * @param assinaturaId ID da assinatura a ser cancelada
     * @return true se a assinatura foi cancelada com sucesso, false caso contrário
     */
    boolean cancelarAssinatura(Long assinaturaId);
    
    void renovarAssinatura(String stripeSubscriptionId);
    void atualizarStatusAssinatura(String stripeSubscriptionId, PlanoAssinatura.StatusAssinatura novoStatus);
    
    /**
     * Atualiza o plano de uma assinatura existente
     * @param assinaturaId ID da assinatura a ser atualizada
     * @param novoPlanoId ID do novo plano
     * @return true se a assinatura foi atualizada com sucesso, false caso contrário
     */
    boolean atualizarPlanoAssinatura(Long assinaturaId, Long novoPlanoId);
}
