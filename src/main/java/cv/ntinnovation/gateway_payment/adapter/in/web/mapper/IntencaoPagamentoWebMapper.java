package cv.ntinnovation.gateway_payment.adapter.in.web.mapper;

import cv.ntinnovation.gateway_payment.adapter.in.web.dto.IntencaoPagamentoRequest;
import cv.ntinnovation.gateway_payment.adapter.in.web.dto.IntencaoPagamentoResponse;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.ContaPagamento;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.IntencaoPagamento;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.MetodoPagamento;
import org.springframework.stereotype.Component;

@Component
public class IntencaoPagamentoWebMapper {

    public IntencaoPagamento toIntencaoPagamento(IntencaoPagamentoRequest request) {
        if (request == null) {
            return null;
        }

        return IntencaoPagamento.builder()
                .amount(request.getAmount())
                .metodoPagamento(MetodoPagamento.builder().id(request.getMetodoPagamentoId()).build())
                .contaPagamentoEmail(request.getEmail())
                .contaPagamentoBillAddrCity(request.getBillAddrCity())
                .contaPagamentoBillAddrCountry(request.getBillAddrCountry())
                .contaPagamentoBillAddrLine1(request.getBillAddrLine1())
                .contaPagamentoBillAddrPostCode(request.getBillAddrPostCode())
                .currency(request.getCurrency())
                .build();
    }

    public IntencaoPagamentoResponse toIntencaoPagamentoResponse(IntencaoPagamento intencaoPagamento) {
        if (intencaoPagamento == null) {
            return null;
        }

        return IntencaoPagamentoResponse.builder()
                .amount(intencaoPagamento.getAmount())
                .status(intencaoPagamento.getStatus())
                .reference(intencaoPagamento.getReference())
                .createdAt(intencaoPagamento.getCreatedAt())
                .currency(intencaoPagamento.getCurrency())
                .build();
    }
}
