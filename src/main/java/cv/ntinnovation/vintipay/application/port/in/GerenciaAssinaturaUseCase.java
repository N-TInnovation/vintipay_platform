package cv.ntinnovation.vintipay.application.port.in;

import cv.ntinnovation.vintipay.application.core.domain.entity.PlanoAssinatura;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;

import java.util.Optional;

public interface GerenciaAssinaturaUseCase {
    PlanoAssinatura criarAssinatura(Usuario usuario, Long planoId);
    Optional<PlanoAssinatura> buscarAssinaturaPorUsuario(Usuario usuario);
    void cancelarAssinatura(Usuario usuario);
    void renovarAssinatura(String stripeSubscriptionId);
    void atualizarStatusAssinatura(String stripeSubscriptionId, PlanoAssinatura.StatusAssinatura novoStatus);
}
