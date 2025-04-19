package cv.ntinnovation.gateway_payment.application.core.usecase;

import cv.ntinnovation.gateway_payment.application.core.domain.rules.RetornoFingerprint;
import cv.ntinnovation.gateway_payment.application.port.in.ValidaRetornoUseCase;
import org.springframework.stereotype.Service;

@Service
public class ValidaRetornoUseCaseImpl implements ValidaRetornoUseCase {
    @Override
    public void executar(RetornoFingerprint retornoFingerprint) {

    }
}
