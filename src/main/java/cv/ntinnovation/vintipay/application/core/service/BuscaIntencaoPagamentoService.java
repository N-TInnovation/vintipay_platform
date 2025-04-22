package cv.ntinnovation.vintipay.application.core.service;

import cv.ntinnovation.vintipay.application.core.domain.entity.IntencaoPagamento;
import cv.ntinnovation.vintipay.application.core.domain.exception.IntencaoPagamentoNaoExisteExeption;
import cv.ntinnovation.vintipay.application.port.in.BuscaIntencaoPagamentoUseCase;
import cv.ntinnovation.vintipay.application.port.out.BuscaIntencaoPagamentoPort;
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
                .orElseThrow(() -> new IntencaoPagamentoNaoExisteExeption("Intenção de pagamento não encontrada: " + reference));
    }
}
