package cv.ntinnovation.vintipay.adapter.out.client.api.vintipay;

import cv.ntinnovation.vintipay.adapter.out.client.api.vintipay.mapper.IntensaoPagamentoMapper;
import cv.ntinnovation.vintipay.adapter.out.client.api.vintipay.request.IntencaoPagamentoRequest;
import cv.ntinnovation.vintipay.adapter.out.client.api.vintipay.response.IntencaoPagamentoResponse;
import cv.ntinnovation.vintipay.application.core.domain.entity.IntencaoPagamento;
import cv.ntinnovation.vintipay.application.port.out.CriaIntencaoPagamentoApiPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CriaIntencaoPagamentoApiAdapter implements CriaIntencaoPagamentoApiPort {
    private final IntensaoPagamentoMapper intensaoPagamentoMapper;
    private final Vintipay vintipay;

    @Override
    public void processar(IntencaoPagamento intencaoPagamento, String token) {
        var request = IntencaoPagamentoRequest.builder()
                .email(intencaoPagamento.getContaPagamentoEmail())
                .merchantSession(intencaoPagamento.getMerchantSession())
                .merchantRef(intencaoPagamento.getMerchantRef())
                .currency(intencaoPagamento.getMoeda())
                .entityCode(intencaoPagamento.getEntityCode())
                .referenceNumber(intencaoPagamento.getReferenceNumber())
                .amount(intencaoPagamento.getValor())
                .billAddrCity(intencaoPagamento.getContaPagamentoBillAddrCity())
                .billAddrCountry(intencaoPagamento.getContaPagamentoBillAddrCountry())
                .billAddrLine1(intencaoPagamento.getContaPagamentoBillAddrLine1())
                .billAddrPostCode(intencaoPagamento.getContaPagamentoBillAddrPostCode())
                .phoneCode(intencaoPagamento.getContaPagamento().getMobilePhone().getCc())
                .phoneNumber(intencaoPagamento.getContaPagamento().getMobilePhone().getSubscriber())
                .build();
        IntencaoPagamentoResponse response = vintipay.criarIntencaoPagamento("Bearer "+token,request);
        intensaoPagamentoMapper.toDomain(intencaoPagamento,response);

    }
}
