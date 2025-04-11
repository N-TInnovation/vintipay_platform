package cv.ntinnovation.gateway_payment.application.port.out;

import cv.ntinnovation.gateway_payment.application.core.domain.entity.IntencaoPagamento;

import java.util.Optional;

public interface BuscaIntencaoPagamentoPort {
    Optional<IntencaoPagamento> buscarPorReferencia(String reference);
}
