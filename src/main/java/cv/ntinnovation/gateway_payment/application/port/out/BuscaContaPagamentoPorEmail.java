package cv.ntinnovation.gateway_payment.application.port.out;

import cv.ntinnovation.gateway_payment.application.core.domain.entity.ContaPagamento;

public interface BuscaContaPagamentoPorEmail {
    ContaPagamento buscarPorEmail(String email);
}
