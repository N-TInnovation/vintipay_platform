package cv.ntinnovation.gateway_payment.application.port.out;

import cv.ntinnovation.gateway_payment.application.core.domain.entity.Pagamento;

import java.util.Optional;

public interface BuscaPagamentoPort {
    Optional<Pagamento> buscarPorMerchantRef(String merchantRef);
}
