package cv.ntinnovation.vintipay.application.port.in;

import cv.ntinnovation.vintipay.application.core.domain.rules.RetornoFingerprint;

public interface ValidaRetornoUseCase {
    void executar(RetornoFingerprint retornoFingerprint);
}
