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
    
    Optional<PlanoAssinatura> buscarAssinaturaPorUsuario(Usuario usuario);
    void cancelarAssinatura(Usuario usuario);
    void renovarAssinatura(String stripeSubscriptionId);
    void atualizarStatusAssinatura(String stripeSubscriptionId, PlanoAssinatura.StatusAssinatura novoStatus);
}
