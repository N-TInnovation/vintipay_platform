package cv.ntinnovation.vintipay.application.port.out;

import cv.ntinnovation.vintipay.application.core.domain.entity.TokenSessao;

public interface SalvarTokenSessao {
    public void executar(TokenSessao tokenSessao);
}
