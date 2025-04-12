package cv.ntinnovation.gateway_payment.application.core.service;

import cv.ntinnovation.gateway_payment.application.core.domain.entity.IntencaoPagamento;
import cv.ntinnovation.gateway_payment.application.port.in.BuscaIntencaoPagamentoUseCase;
import cv.ntinnovation.gateway_payment.application.port.out.BuscaIntencaoPagamentoPort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Qualifier("buscaIntencaoPagamentoService")
public class BuscaIntencaoPagamentoService implements BuscaIntencaoPagamentoUseCase {

    private final BuscaIntencaoPagamentoPort buscaIntencaoPagamentoPort;

    @Override
    public IntencaoPagamento buscarPorReferencia(String reference) {
        return buscaIntencaoPagamentoPort.buscarPorReferencia(reference)
                .orElseThrow(() -> new RuntimeException("Intenção de pagamento não encontrada: " + reference));
    }
}
