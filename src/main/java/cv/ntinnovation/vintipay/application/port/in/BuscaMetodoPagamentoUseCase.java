package cv.ntinnovation.vintipay.application.port.in;

import cv.ntinnovation.vintipay.application.core.domain.entity.MetodoPagamento;

import java.util.List;
import java.util.Optional;

public interface BuscaMetodoPagamentoUseCase {
    List<MetodoPagamento> listarTodos();
    Optional<MetodoPagamento> buscarPorId(Long id);
}
