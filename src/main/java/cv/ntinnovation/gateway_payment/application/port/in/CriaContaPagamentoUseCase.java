package cv.ntinnovation.gateway_payment.application.port.in;

import cv.ntinnovation.gateway_payment.application.core.domain.entity.ContaPagamento;

public interface CriaContaPagamentoUseCase {
    ContaPagamento criarOuBuscarPorEmail(String email, String billAddrCity, String billAddrCountry, String billAddrLine1, String billAddrPostCode);
}
