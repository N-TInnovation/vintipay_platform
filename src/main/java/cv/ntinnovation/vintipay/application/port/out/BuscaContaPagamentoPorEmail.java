package cv.ntinnovation.vintipay.application.port.out;

import cv.ntinnovation.vintipay.application.core.domain.entity.ContaPagamento;

public interface BuscaContaPagamentoPorEmail {
    ContaPagamento buscarPorEmail(String email);
}
