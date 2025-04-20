package cv.ntinnovation.gateway_payment.application.core.usecase;

import cv.ntinnovation.gateway_payment.application.core.domain.entity.Pagamento;
import cv.ntinnovation.gateway_payment.application.core.domain.enums.StatusPagamentoEnum;
import cv.ntinnovation.gateway_payment.application.port.in.AtualizaStatusPagamentoUseCase;
import cv.ntinnovation.gateway_payment.application.port.out.BuscaPagamentoPort;
import cv.ntinnovation.gateway_payment.application.port.out.SalvaIntencaoPagamento;
import cv.ntinnovation.gateway_payment.application.port.out.SalvaPagamento;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AtualizaStatusPagamentoUseCaseImpl implements AtualizaStatusPagamentoUseCase {

    private final BuscaPagamentoPort buscaPagamentoPort;
    private final SalvaPagamento salvaPagamento;
    private final SalvaIntencaoPagamento salvaIntencaoPagamento;

    @Scheduled(fixedRate = 10000)
    public void cancelarPagamentosPendentes() {
        buscaPagamentoPort.buscarTodos(null, null, StatusPagamentoEnum.PENDENTE)
                .getContent().forEach(pagamento ->{
                    var tempoInicial = pagamento.getIntencaoPagamento().getCreatedAt();
                    if (Duration.between(tempoInicial, LocalDateTime.now()).toMinutes() >= 10) {
                        atualizar(pagamento.getReference(),
                                StatusPagamentoEnum.CANCELADO, "Transação cancelada pelo Sistema", null, null);
                    }
                });
    }

    @Override
    @Transactional
    public Pagamento atualizar(String reference, StatusPagamentoEnum status, String message, String fingerprint, String pan) {
        var pagamento = buscaPagamentoPort.buscarPorReferencia(reference)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado: " + reference));

        pagamento.getIntencaoPagamento().setStatus(status);
        pagamento.setStatus(status);
        pagamento.setMensagem(message);
        pagamento.setReference(reference);
        pagamento.setPanMascarado(pan);
        pagamento.setResultFingerPrint(fingerprint);

        salvaIntencaoPagamento.salvar(pagamento.getIntencaoPagamento());

        return salvaPagamento.salvar(pagamento);
    }
}
