package cv.ntinnovation.gateway_payment.application.core.usecase;

import cv.ntinnovation.gateway_payment.application.core.domain.rules.FingerPrintService;
import cv.ntinnovation.gateway_payment.application.core.domain.rules.RetornoFingerprint;
import cv.ntinnovation.gateway_payment.application.port.in.BuscaConfiguracaoPagamentoUseCase;
import cv.ntinnovation.gateway_payment.application.port.in.ValidaRetornoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ValidaRetornoUseCaseImpl implements ValidaRetornoUseCase {
    private final FingerPrintService fingerPrintService;

    @Override
    public void executar(RetornoFingerprint retornoFingerprint) {
        retornoFingerprint.validarFingerPrint(fingerPrintService);
    }
}
