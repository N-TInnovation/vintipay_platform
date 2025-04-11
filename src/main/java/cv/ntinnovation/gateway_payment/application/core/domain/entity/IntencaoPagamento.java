package cv.ntinnovation.gateway_payment.application.core.domain.entity;

import cv.ntinnovation.gateway_payment.application.core.domain.enums.StatusPagamentoEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntencaoPagamento {
    private Long id;
    private BigDecimal amount;
    private StatusPagamentoEnum status;
    private String reference;
    private LocalDateTime createdAt;
    private String currency;
    private MetodoPagamento metodoPagamento;
    private ContaPagamento contaPagamento;
    
    // Campos para criação de conta de pagamento
    private String contaPagamentoEmail;
    private String contaPagamentoBillAddrCity;
    private String contaPagamentoBillAddrCountry;
    private String contaPagamentoBillAddrLine1;
    private String contaPagamentoBillAddrPostCode;
    
    private String merchantRef;
    private String merchantSession;
    private String posId;
    private String entityCode;
    private String transactionCode;
    private String referenceNumber;
    private String timestamp;
}
