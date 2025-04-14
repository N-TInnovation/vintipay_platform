package cv.ntinnovation.gateway_payment.application.port.in;

import cv.ntinnovation.gateway_payment.application.core.domain.entity.Pagamento;

import java.util.Optional;

public interface BuscaPagamentoUseCase {
    Optional<Pagamento> buscarPorReference(String reference);
}
