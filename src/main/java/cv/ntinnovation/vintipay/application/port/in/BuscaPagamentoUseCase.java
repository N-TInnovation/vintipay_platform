package cv.ntinnovation.vintipay.application.port.in;

import cv.ntinnovation.vintipay.application.core.domain.entity.Pagamento;
import cv.ntinnovation.vintipay.application.core.domain.enums.AmbienteEnum;
import cv.ntinnovation.vintipay.application.core.domain.enums.StatusPagamentoEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface BuscaPagamentoUseCase {
    Page<Pagamento> buscarTodos(Pageable pageable, String reference, StatusPagamentoEnum status);
    Optional<Pagamento> buscarPorReferencia(String reference);
    Page<Pagamento> buscarPorUsuario(Pageable pageable, Long usuarioId, StatusPagamentoEnum status);
    Page<Pagamento> buscarPorUsuarioEAmbiente(Pageable pageable, Long usuarioId, StatusPagamentoEnum status, AmbienteEnum ambiente);
}
