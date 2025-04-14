package cv.ntinnovation.gateway_payment.application.port.in;

import cv.ntinnovation.gateway_payment.application.core.domain.entity.Pagamento;
import cv.ntinnovation.gateway_payment.application.core.domain.enums.StatusPagamentoEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface BuscaPagamentoUseCase {
    Page<Pagamento> buscarTodos(Pageable pageable, String reference, StatusPagamentoEnum status);
    Optional<Pagamento> buscarPorReferencia(String reference);
}
