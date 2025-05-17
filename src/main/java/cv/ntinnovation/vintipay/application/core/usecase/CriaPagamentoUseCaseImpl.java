package cv.ntinnovation.vintipay.application.core.usecase;

import cv.ntinnovation.vintipay.application.core.domain.entity.DataFingerPrint;
import cv.ntinnovation.vintipay.application.core.domain.entity.FingerPrintResult;
import cv.ntinnovation.vintipay.application.core.domain.entity.IntencaoPagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.Pagamento;
import cv.ntinnovation.vintipay.application.core.domain.enums.MoedaEnum;
import cv.ntinnovation.vintipay.application.core.domain.enums.StatusIntencaoPagamentoEnum;
import cv.ntinnovation.vintipay.application.core.domain.enums.StatusPagamentoEnum;
import cv.ntinnovation.vintipay.application.core.domain.exception.IntencaoPagamentoNaoExisteExeption;
import cv.ntinnovation.vintipay.application.core.domain.rules.FingerPrintService;
import cv.ntinnovation.vintipay.application.port.in.BuscaConfiguracaoPagamentoUseCase;
import cv.ntinnovation.vintipay.application.port.in.BuscaIntencaoPagamentoUseCase;
import cv.ntinnovation.vintipay.application.port.in.CriaPagamentoUseCase;
import cv.ntinnovation.vintipay.application.port.out.SalvaIntencaoPagamento;
import cv.ntinnovation.vintipay.application.port.out.SalvaPagamento;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CriaPagamentoUseCaseImpl implements CriaPagamentoUseCase {

    @Qualifier("buscaIntencaoPagamentoService")
    private final BuscaConfiguracaoPagamentoUseCase buscaConfiguracaoPagamentoUseCase;
    private final SalvaPagamento salvaPagamento;
    private final FingerPrintService fingerPrintService;
    private final SalvaIntencaoPagamento salvaIntencaoPagamento;

    @Override
    @Transactional
    public Pagamento criar(IntencaoPagamento intencaoPagamento) {
        // Buscar a intenção de pagamento pela referência

        if (intencaoPagamento == null ) {
            throw new IntencaoPagamentoNaoExisteExeption("Intenção de pagamento não encontrada: " + intencaoPagamento.getReferencia());
        }

        if(!intencaoPagamento.getStatus().equals(StatusIntencaoPagamentoEnum.PENDENTE)) {
            throw new RuntimeException("Interação de pagamento com status diferente de pendente: " + intencaoPagamento);
        }

        // Criar o pagamento
        Pagamento pagamento = Pagamento.builder()
                .intencaoPagamento(intencaoPagamento)
                .reference(intencaoPagamento.getReferencia())
                .mensagem("Aguardando pagamento")
                .ambiente(intencaoPagamento.getAmbiente())
                .build();

        pagamento.setStatus(intencaoPagamento.getStatus().toStatusPagamento());

        // Gerar timestamp
        pagamento.gerarTimestamp();

        // Buscar configuração de pagamento pelo posId da intenção
        var configuracaoPagamento = buscaConfiguracaoPagamentoUseCase.buscarPorPosId(intencaoPagamento.getPosId());

        // Gerar fingerprint usando o FingerPrintService
        DataFingerPrint dataFingerPrint = DataFingerPrint.builder()
                .posAutCode(configuracaoPagamento.getPosAutCode())
                .timestamp(pagamento.getTimestamp())
                .amount(intencaoPagamento.getValor())
                .merchantRef(intencaoPagamento.getMerchantRef())
                .merchantSession(intencaoPagamento.getMerchantSession())
                .posID(intencaoPagamento.getPosId())
                .currency(MoedaEnum.getByName(intencaoPagamento.getMoeda()).getCodigoISO())
                .transactionCode(intencaoPagamento.getTransactionCode())
                .entityCode(intencaoPagamento.getEntityCode() == null ? "" : intencaoPagamento.getEntityCode())
                .referenceNumber(intencaoPagamento.getReferenceNumber() == null ? "" : intencaoPagamento.getReferenceNumber().toString())
                .build();

        FingerPrintResult fingerPrintResult = fingerPrintService.generateFingerPrint(dataFingerPrint);
        pagamento.setFingerprint(fingerPrintResult.getHashFingerPrint());

        intencaoPagamento.setStatus(StatusIntencaoPagamentoEnum.fromStatusPagamento(StatusPagamentoEnum.PROCESSANDO));

        salvaIntencaoPagamento.salvar(intencaoPagamento);

        // Salvar o pagamento
        return salvaPagamento.salvar(pagamento);
    }
}
