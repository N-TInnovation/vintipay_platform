package cv.ntinnovation.gateway_payment.application.port.in;

import cv.ntinnovation.gateway_payment.application.core.domain.entity.MetodoPagamento;

import java.util.List;
import java.util.Optional;

public interface BuscaMetodoPagamentoUseCase {
    List<MetodoPagamento> listarTodos();
    Optional<MetodoPagamento> buscarPorId(Long id);
}
