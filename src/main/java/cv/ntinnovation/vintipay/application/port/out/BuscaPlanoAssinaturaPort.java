package cv.ntinnovation.vintipay.application.port.out;

import cv.ntinnovation.vintipay.application.core.domain.entity.PlanoAssinatura;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;

import java.util.List;
import java.util.Optional;

public interface BuscaPlanoAssinaturaPort {
    Optional<PlanoAssinatura> buscarPorUsuario(Usuario usuario);
    Optional<PlanoAssinatura> buscarPorStripeSubscriptionId(String stripeSubscriptionId);
    List<PlanoAssinatura> buscarPorStatus(PlanoAssinatura.StatusAssinatura status);
}
