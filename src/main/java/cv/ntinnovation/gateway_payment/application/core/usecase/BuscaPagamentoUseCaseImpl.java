package cv.ntinnovation.gateway_payment.application.core.usecase;

import cv.ntinnovation.gateway_payment.application.core.domain.entity.Pagamento;
import cv.ntinnovation.gateway_payment.application.port.in.BuscaPagamentoUseCase;
import cv.ntinnovation.gateway_payment.application.port.out.BuscaPagamentoPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BuscaPagamentoUseCaseImpl implements BuscaPagamentoUseCase {

    private final BuscaPagamentoPort buscaPagamentoPort;

    @Override
    public Optional<Pagamento> buscarPorReference(String reference) {
        return buscaPagamentoPort.buscarPorReference(reference);
    }

}
