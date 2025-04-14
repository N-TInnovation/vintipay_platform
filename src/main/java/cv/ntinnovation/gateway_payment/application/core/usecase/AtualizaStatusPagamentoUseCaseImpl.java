package cv.ntinnovation.gateway_payment.application.core.usecase;

import cv.ntinnovation.gateway_payment.application.core.domain.entity.Pagamento;
import cv.ntinnovation.gateway_payment.application.core.domain.enums.StatusPagamentoEnum;
import cv.ntinnovation.gateway_payment.application.port.in.AtualizaStatusPagamentoUseCase;
import cv.ntinnovation.gateway_payment.application.port.out.BuscaPagamentoPort;
import cv.ntinnovation.gateway_payment.application.port.out.SalvaPagamento;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AtualizaStatusPagamentoUseCaseImpl implements AtualizaStatusPagamentoUseCase {

    private final BuscaPagamentoPort buscaPagamentoPort;
    private final SalvaPagamento salvaPagamento;

    @Override
    @Transactional
    public Pagamento atualizar(String reference, StatusPagamentoEnum status, String message, String authorizationCode, String pan) {
        var pagamento = buscaPagamentoPort.buscarPorReference(reference)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado: " + reference));

        pagamento.getIntencaoPagamento().setStatus(status);
        pagamento.setStatus(status);
        pagamento.setMensagem(message);
        pagamento.setCodigoAutorizacao(authorizationCode);
        pagamento.setPanMascarado(pan);

        return salvaPagamento.salvar(pagamento);
    }
}
