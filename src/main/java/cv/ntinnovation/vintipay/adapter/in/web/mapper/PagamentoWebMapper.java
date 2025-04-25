package cv.ntinnovation.vintipay.adapter.in.web.mapper;

import cv.ntinnovation.vintipay.adapter.in.web.response.IntencaoPagamentoResponse;
import cv.ntinnovation.vintipay.adapter.in.web.response.PagamentoResponse;
import cv.ntinnovation.vintipay.application.core.domain.entity.ContaPagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.IntencaoPagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.Pagamento;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PagamentoWebMapper {

    public PagamentoResponse toResponse(Pagamento pagamento) {
        return PagamentoResponse.builder()
                .id(pagamento.getId())
                .reference(pagamento.getReference())
                .fingerprint(pagamento.getFingerprint())
                .timestamp(pagamento.getTimestamp())
                .status(pagamento.getStatus())
                .mensagem(pagamento.getMensagem())
                .panMascarado(pagamento.getPanMascarado())
                .createdAt(pagamento.getCreatedAt())
                .intencaoPagamento(toIntencaoPagamentoResponse(pagamento.getIntencaoPagamento()))
                .build();
    }

    private IntencaoPagamentoResponse toIntencaoPagamentoResponse(IntencaoPagamento intencaoPagamento) {
        if (intencaoPagamento == null) {
            return null;
        }

        ContaPagamento contaPagamento = intencaoPagamento.getContaPagamento();

        return IntencaoPagamentoResponse.builder()
                .id(intencaoPagamento.getId())
                .reference(intencaoPagamento.getReferencia())
                .amount(intencaoPagamento.getValor())
                .currency(intencaoPagamento.getMoeda())
                .status(intencaoPagamento.getStatus().toStatusPagamento())
                .createdAt(intencaoPagamento.getDataCriacao())
                .merchantRef(intencaoPagamento.getMerchantRef())
                .merchantSession(intencaoPagamento.getMerchantSession())
                .posId(intencaoPagamento.getPosId())
                .timestamp(intencaoPagamento.getTimestamp())
                .entityCode(intencaoPagamento.getEntityCode())
                .referenceNumber(intencaoPagamento.getReferenceNumber()+"")
                .transactionCode(intencaoPagamento.getTransactionCode())
                // Dados da conta de pagamento
                .email(contaPagamento != null ? contaPagamento.getEmail() : null)
                .workPhoneCc(contaPagamento != null && contaPagamento.getWorkPhone() != null ? contaPagamento.getWorkPhone().getCc() : null)
                .workPhoneSubscriber(contaPagamento != null && contaPagamento.getWorkPhone() != null ? contaPagamento.getWorkPhone().getSubscriber() : null)
                .mobilePhoneCc(contaPagamento != null && contaPagamento.getMobilePhone() != null ? contaPagamento.getMobilePhone().getCc() : null)
                .mobilePhoneSubscriber(contaPagamento != null && contaPagamento.getMobilePhone() != null ? contaPagamento.getMobilePhone().getSubscriber() : null)
                .billAddrCity(contaPagamento != null ? contaPagamento.getBillAddrCity() : null)
                .billAddrCountry(contaPagamento != null ? contaPagamento.getBillAddrCountry() : null)
                .billAddrLine1(contaPagamento != null ? contaPagamento.getBillAddrLine1() : null)
                .billAddrLine2(contaPagamento != null ? contaPagamento.getBillAddrLine2() : null)
                .billAddrLine3(contaPagamento != null ? contaPagamento.getBillAddrLine3() : null)
                .billAddrPostCode(contaPagamento != null ? contaPagamento.getBillAddrPostCode() : null)
                .billAddrState(contaPagamento != null ? contaPagamento.getBillAddrState() : null)
                .build();
    }

    public List<PagamentoResponse> toResponseList(List<Pagamento> pagamentos) {
        return pagamentos.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
