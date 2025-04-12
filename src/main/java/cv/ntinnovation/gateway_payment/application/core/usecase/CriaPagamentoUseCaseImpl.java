package cv.ntinnovation.gateway_payment.application.core.usecase;

import cv.ntinnovation.gateway_payment.application.core.domain.entity.DataFingerPrint;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.FingerPrintResult;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.IntencaoPagamento;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.Pagamento;
import cv.ntinnovation.gateway_payment.application.core.service.FingerPrintService;
import cv.ntinnovation.gateway_payment.application.port.in.BuscaConfiguracaoPagamentoUseCase;
import cv.ntinnovation.gateway_payment.application.port.in.BuscaIntencaoPagamentoUseCase;
import cv.ntinnovation.gateway_payment.application.port.in.CriaPagamentoUseCase;
import cv.ntinnovation.gateway_payment.application.port.out.SalvaPagamento;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CriaPagamentoUseCaseImpl implements CriaPagamentoUseCase {

    @Qualifier("buscaIntencaoPagamentoService")
    private final BuscaIntencaoPagamentoUseCase buscaIntencaoPagamentoUseCase;
    private final BuscaConfiguracaoPagamentoUseCase buscaConfiguracaoPagamentoUseCase;
    private final SalvaPagamento salvaPagamento;
    private final FingerPrintService fingerPrintService;

    @Override
    @Transactional
    public Pagamento criar(String intencaoPagamentoRef) {
        // Buscar a intenção de pagamento pela referência
        var intencaoPagamento= buscaIntencaoPagamentoUseCase.buscarPorReferencia(intencaoPagamentoRef);
        if (intencaoPagamento == null) {
            throw new RuntimeException("Intenção de pagamento não encontrada: " + intencaoPagamentoRef);
        }

        // Criar o pagamento
        Pagamento pagamento = Pagamento.builder()
                .intencaoPagamento(intencaoPagamento)
                .build();

        // Gerar timestamp
        pagamento.gerarTimestamp();

        // Buscar configuração de pagamento pelo posId da intenção
        var configuracaoPagamento = buscaConfiguracaoPagamentoUseCase.buscarPorPosId(intencaoPagamento.getPosId());

        // Gerar fingerprint usando o FingerPrintService
        DataFingerPrint dataFingerPrint = DataFingerPrint.builder()
                .posAutCode(configuracaoPagamento.getPosAutCode())
                .timestamp(pagamento.getTimestamp())
                .amount(intencaoPagamento.getAmount())
                .merchantRef(intencaoPagamento.getMerchantRef())
                .merchantSession(intencaoPagamento.getMerchantSession())
                .posID(intencaoPagamento.getPosId())
                .currency(intencaoPagamento.getCurrency())
                .transactionCode(intencaoPagamento.getTransactionCode())
                .entityCode(intencaoPagamento.getEntityCode())
                .referenceNumber(intencaoPagamento.getReferenceNumber().toString())
                .build();

        FingerPrintResult fingerPrintResult = fingerPrintService.generateFingerPrint(dataFingerPrint);
        pagamento.setFingerprint(fingerPrintResult.getHashFingerPrint());

        // Salvar o pagamento
        return salvaPagamento.salvar(pagamento);
    }
}
