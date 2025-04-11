package cv.ntinnovation.gateway_payment.application.port.out;

import cv.ntinnovation.gateway_payment.application.core.domain.entity.MetodoPagamento;

import java.util.List;
import java.util.Optional;

public interface BuscaMetodoPagamentoPort {
    List<MetodoPagamento> buscarTodos();
    Optional<MetodoPagamento> buscarPorId(Long id);
    Optional<MetodoPagamento> buscarPorNome(String nome);
}
