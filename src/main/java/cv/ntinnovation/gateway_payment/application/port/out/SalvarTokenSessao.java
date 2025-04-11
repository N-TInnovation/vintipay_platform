package cv.ntinnovation.gateway_payment.application.port.out;

import cv.ntinnovation.gateway_payment.application.core.domain.entity.TokenSessao;

public interface SalvarTokenSessao {
    public void executar(TokenSessao tokenSessao);
}
