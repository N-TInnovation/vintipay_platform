package cv.ntinnovation.vintipay.application.core.usecase;

import cv.ntinnovation.vintipay.application.core.domain.entity.IntencaoPagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.SubscricaoPagamento;
import cv.ntinnovation.vintipay.application.port.in.CriaSubscricaoPagamentoUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CriaSubscricaoPagamentoUseCaseImpl implements CriaSubscricaoPagamentoUseCase {
    @Override
    public SubscricaoPagamento subscrever(IntencaoPagamento intencaoPagamento) {

        return null;
    }
}
