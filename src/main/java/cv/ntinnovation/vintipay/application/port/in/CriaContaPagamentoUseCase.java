package cv.ntinnovation.vintipay.application.port.in;

import cv.ntinnovation.vintipay.application.core.domain.entity.ContaPagamento;

public interface CriaContaPagamentoUseCase {
    ContaPagamento criarOuBuscarPorEmail(String name, String email, String billAddrCity, String billAddrState, String billAddrCountry, String billAddrLine1, String billAddrPostCode, String phoneCode, String phoneNumber);
}
