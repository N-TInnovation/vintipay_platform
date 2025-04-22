package cv.ntinnovation.vintipay.application.core.usecase;

import cv.ntinnovation.vintipay.application.core.domain.rules.FingerPrintService;
import cv.ntinnovation.vintipay.application.core.domain.rules.RetornoFingerprint;
import cv.ntinnovation.vintipay.application.port.in.ValidaRetornoUseCase;
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
