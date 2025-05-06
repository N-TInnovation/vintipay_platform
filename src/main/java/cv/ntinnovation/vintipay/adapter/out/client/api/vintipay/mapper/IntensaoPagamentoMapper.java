package cv.ntinnovation.vintipay.adapter.out.client.api.vintipay.mapper;

import cv.ntinnovation.vintipay.adapter.out.client.api.vintipay.response.IntencaoPagamentoResponse;
import cv.ntinnovation.vintipay.application.core.domain.entity.IntencaoPagamento;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class IntensaoPagamentoMapper {

    public void toDomain(IntencaoPagamento intencaoPagamento, IntencaoPagamentoResponse intencaoPagamentoResponse){

        intencaoPagamento.setReferencia(intencaoPagamentoResponse.getReference());
        intencaoPagamento.setStatus(intencaoPagamentoResponse.getStatus());
        intencaoPagamento.setMerchantSession(intencaoPagamentoResponse.getSessionId());


    }
}
