package cv.ntinnovation.vintipay.application.port.out;

import cv.ntinnovation.vintipay.application.core.domain.entity.TokenSessao;

public interface EncontraSessao {
    public TokenSessao getTokenSessao(String sessionCode);
}
