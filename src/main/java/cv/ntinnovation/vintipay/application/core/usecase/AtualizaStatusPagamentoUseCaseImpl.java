package cv.ntinnovation.vintipay.application.core.usecase;

import cv.ntinnovation.vintipay.application.core.domain.entity.Pagamento;
import cv.ntinnovation.vintipay.application.core.domain.enums.StatusIntencaoPagamentoEnum;
import cv.ntinnovation.vintipay.application.core.domain.enums.StatusPagamentoEnum;
import cv.ntinnovation.vintipay.application.core.domain.exception.PagamentoNaoEncontradoException;
import cv.ntinnovation.vintipay.application.port.in.AtualizaStatusPagamentoUseCase;
import cv.ntinnovation.vintipay.application.port.out.BuscaPagamentoPort;
import cv.ntinnovation.vintipay.application.port.out.BuscaSessaoPagamentoPort;
import cv.ntinnovation.vintipay.application.port.out.SalvaIntencaoPagamento;
import cv.ntinnovation.vintipay.application.port.out.SalvaPagamento;
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

    @Transactional
    public void expirarPagamentosPendentes() {
        buscaPagamentoPort.buscarTodos(null, null, StatusPagamentoEnum.PENDENTE)
                .getContent().forEach(pagamento ->{
                     atualizar(pagamento.getReference(),
                                StatusPagamentoEnum.EXPIRADO, "Transação expirada pela Sessão de Pagamento", null, null, null, null);
                });
    }

    @Override
    @Transactional
    public Pagamento atualizar(String reference, StatusPagamentoEnum status, String message, String fingerprint, String pan, String clearingPeriod, String transactionId) {
        var pagamento = buscaPagamentoPort.buscarPorReferencia(reference)
                .orElseThrow(() -> new PagamentoNaoEncontradoException("Pagamento não encontrado: " + reference));

        pagamento.getIntencaoPagamento().setStatus(StatusIntencaoPagamentoEnum.fromStatusPagamento(status));
        pagamento.setStatus(status);
        pagamento.setMensagem(message);
        pagamento.setReference(reference);
        if(pan != null)
            pagamento.setPanMascarado(pan);
        pagamento.setResultFingerPrint(fingerprint);
        if(clearingPeriod != null)
            pagamento.setClearingPeriod(clearingPeriod);
        if(transactionId != null)
            pagamento.setTransactionId(transactionId);

        salvaIntencaoPagamento.salvar(pagamento.getIntencaoPagamento());

        return salvaPagamento.salvar(pagamento);
    }
}
