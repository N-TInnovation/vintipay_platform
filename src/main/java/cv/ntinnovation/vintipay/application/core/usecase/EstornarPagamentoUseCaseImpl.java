package cv.ntinnovation.vintipay.application.core.usecase;

import cv.ntinnovation.vintipay.application.core.domain.entity.DataFingerPrint;
import cv.ntinnovation.vintipay.application.core.domain.entity.FingerPrintResult;
import cv.ntinnovation.vintipay.application.core.domain.entity.Pagamento;
import cv.ntinnovation.vintipay.application.core.domain.enums.IntencaoPagamentoEnum;
import cv.ntinnovation.vintipay.application.core.domain.enums.MoedaEnum;
import cv.ntinnovation.vintipay.application.core.domain.rules.FingerPrintService;
import cv.ntinnovation.vintipay.application.port.in.BuscaConfiguracaoPagamentoUseCase;
import cv.ntinnovation.vintipay.application.port.in.BuscaPagamentoUseCase;
import cv.ntinnovation.vintipay.application.port.in.EstornarPagamentoUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EstornarPagamentoUseCaseImpl implements EstornarPagamentoUseCase {

    private final BuscaPagamentoUseCase buscaPagamentoUseCase;
    private final BuscaConfiguracaoPagamentoUseCase buscaConfiguracaoPagamentoUseCase;
    private final FingerPrintService fingerPrintService;

    @Override
    public Pagamento estornar(String reference) {
        var pagamento = buscaPagamentoUseCase.buscarPorReferencia(reference)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado: " + reference));

        // Gerar timestamp
        pagamento.gerarTimestamp();

        // Buscar configuração de pagamento pelo posId da intenção
        var configuracaoPagamento = buscaConfiguracaoPagamentoUseCase.buscarPorPosId(pagamento.getIntencaoPagamento().getPosId());

        // Gerar fingerprint usando o FingerPrintService
        DataFingerPrint dataFingerPrint = DataFingerPrint.builder()
                .posAutCode(configuracaoPagamento.getPosAutCode())
                .timestamp(pagamento.getTimestamp())
                .amount(pagamento.getIntencaoPagamento().getValor())
                .merchantRef(pagamento.getIntencaoPagamento().getMerchantRef())
                .merchantSession(pagamento.getIntencaoPagamento().getMerchantSession())
                .posID(pagamento.getIntencaoPagamento().getPosId())
                .currency(MoedaEnum.getByName(pagamento.getIntencaoPagamento().getMoeda()).getCodigoISO())
                .transactionCode(IntencaoPagamentoEnum.ESTORNO.getTransactionCode())
                .entityCode(pagamento.getIntencaoPagamento().getEntityCode() == null ? "" : pagamento.getIntencaoPagamento().getEntityCode())
                .referenceNumber(pagamento.getIntencaoPagamento().getReferenceNumber() == null ? "" : pagamento.getIntencaoPagamento().getReferenceNumber().toString())
                .build();

        FingerPrintResult fingerPrintResult = fingerPrintService.generateFingerPrint(dataFingerPrint);
        pagamento.setFingerprint(fingerPrintResult.getHashFingerPrint());

        return pagamento;
    }
}
