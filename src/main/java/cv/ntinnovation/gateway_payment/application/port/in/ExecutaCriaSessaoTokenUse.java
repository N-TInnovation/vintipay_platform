package cv.ntinnovation.gateway_payment.application.port.in;

import cv.ntinnovation.gateway_payment.application.core.domain.entity.TokenSessao;

public interface ExecutaCriaSessaoTokenUse {
    public void executar(TokenSessao tokenSessao);
}
