package cv.ntinnovation.gateway_payment.adapter.in.web.mapper;

import cv.ntinnovation.gateway_payment.adapter.in.web.dto.ContaPagamentoRequest;
import cv.ntinnovation.gateway_payment.adapter.in.web.dto.ContaPagamentoResponse;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.ContaPagamento;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ContaPagamentoWebMapper {

    public ContaPagamento toContaPagamento(ContaPagamentoRequest request) {
        if (request == null) {
            return null;
        }

        return ContaPagamento.builder()
                .email(request.getEmail())
                .billAddrCity(request.getBillAddrCity())
                .billAddrCountry(request.getBillAddrCountry())
                .billAddrLine1(request.getBillAddrLine1())
                .billAddrPostCode(request.getBillAddrPostCode())
                .build();
    }

    public ContaPagamentoResponse toContaPagamentoResponse(ContaPagamento contaPagamento) {
        if (contaPagamento == null) {
            return null;
        }

        return ContaPagamentoResponse.builder()
                .id(contaPagamento.getId())
                .email(contaPagamento.getEmail())
                .billAddrCity(contaPagamento.getBillAddrCity())
                .billAddrCountry(contaPagamento.getBillAddrCountry())
                .billAddrLine1(contaPagamento.getBillAddrLine1())
                .billAddrPostCode(contaPagamento.getBillAddrPostCode())
                .createdAt(LocalDateTime.now())
                .build();
    }
}
