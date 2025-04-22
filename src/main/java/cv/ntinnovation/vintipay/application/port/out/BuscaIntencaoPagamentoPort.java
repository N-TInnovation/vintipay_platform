package cv.ntinnovation.vintipay.application.port.out;

import cv.ntinnovation.vintipay.application.core.domain.entity.IntencaoPagamento;

import java.util.Optional;

public interface BuscaIntencaoPagamentoPort {
    Optional<IntencaoPagamento> buscarPorReferencia(String reference);
}
