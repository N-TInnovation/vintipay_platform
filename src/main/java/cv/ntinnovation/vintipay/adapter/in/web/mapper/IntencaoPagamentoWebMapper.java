package cv.ntinnovation.vintipay.adapter.in.web.mapper;

import cv.ntinnovation.vintipay.adapter.in.web.dto.CompradorInfoRequest;
import cv.ntinnovation.vintipay.adapter.in.web.dto.IntencaoPagamentoRequest;
import cv.ntinnovation.vintipay.adapter.in.web.dto.IntencaoPagamentoResponse;
import cv.ntinnovation.vintipay.application.core.domain.entity.IntencaoPagamento;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

@Component
public class IntencaoPagamentoWebMapper {


    public IntencaoPagamento toIntencaoPagamento(CompradorInfoRequest request) {
        if (request == null) {
            return null;
        }

        return IntencaoPagamento.builder()
                .contaPagamentoEmail(request.getEmail())
                .contaPagamentoBillAddrCity(request.getBillAddrCity())
                .contaPagamentoBillAddrCountry(request.getBillAddrCountry())
                .contaPagamentoBillAddrLine1(request.getBillAddrLine1())
                .contaPagamentoBillAddrPostCode(request.getBillAddrPostCode())
                .merchantSession(request.getMerchantSession())
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build();
    }
    public IntencaoPagamento toIntencaoPagamento(IntencaoPagamentoRequest request) {
        if (request == null) {
            return null;
        }

        return IntencaoPagamento.builder()
                .valor(request.getAmount())
                .contaPagamentoEmail(request.getEmail())
                .contaPagamentoBillAddrCity(request.getBillAddrCity())
                .contaPagamentoBillAddrCountry(request.getBillAddrCountry())
                .contaPagamentoBillAddrLine1(request.getBillAddrLine1())
                .contaPagamentoBillAddrPostCode(request.getBillAddrPostCode())
                .moeda(request.getCurrency())
                .merchantRef(request.getMerchantRef())
                .merchantSession(request.getMerchantSession())
                .entityCode(request.getEntityCode())
                .referenceNumber(request.getReferenceNumber())
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build();
    }

    public IntencaoPagamentoResponse toIntencaoPagamentoResponse(IntencaoPagamento intencaoPagamento) {
        if (intencaoPagamento == null) {
            return null;
        }

        return IntencaoPagamentoResponse.builder()
                .amount(intencaoPagamento.getValor())
                .status(intencaoPagamento.getStatus().toStatusPagamento())
                .reference(intencaoPagamento.getReferencia())
                .createdAt(intencaoPagamento.getDataCriacao())
                .currency(intencaoPagamento.getMoeda())
                .build();
    }
}
