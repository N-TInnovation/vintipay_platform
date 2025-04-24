package cv.ntinnovation.vintipay.application.port.out;

import cv.ntinnovation.vintipay.application.core.domain.entity.Plano;

import java.util.List;
import java.util.Optional;

public interface BuscaPlanoPort {
    List<Plano> buscarTodosAtivos();
    List<Plano> buscarTodos();
    Optional<Plano> buscarPorId(Long id);
    Optional<Plano> buscarPorNome(String nome);
    Optional<Plano> buscarPorStripeProductId(String stripeProductId);
}
