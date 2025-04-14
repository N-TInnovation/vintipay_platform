package cv.ntinnovation.gateway_payment.application.port.out;

import cv.ntinnovation.gateway_payment.application.core.domain.entity.Pagamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface BuscaPagamentoPort {
    Optional<Pagamento> buscarPorReferencia(String reference);
    Page<Pagamento> buscarTodos(Pageable pageable);
}
