package cv.ntinnovation.gateway_payment.application.port.in;

import cv.ntinnovation.gateway_payment.application.core.domain.rules.RetornoFingerprint;

public interface ValidaRetornoUseCase {
    void executar(RetornoFingerprint retornoFingerprint);
}
