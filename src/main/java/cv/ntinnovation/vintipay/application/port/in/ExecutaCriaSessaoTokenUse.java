package cv.ntinnovation.vintipay.application.port.in;

import cv.ntinnovation.vintipay.application.core.domain.entity.TokenSessao;

public interface ExecutaCriaSessaoTokenUse {
    public void executar(TokenSessao tokenSessao);
}
