package cv.ntinnovation.gateway_payment.adapter.in.web.response;

import cv.ntinnovation.gateway_payment.application.core.domain.enums.StatusPagamentoEnum;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class IntencaoPagamentoResponse {
    private Long id;
    private String reference;
    private BigDecimal amount;
    private String currency;
    private StatusPagamentoEnum status;
    private LocalDateTime createdAt;
    private String merchantRef;
    private String merchantSession;
    private String posId;
    private String timestamp;
    private String entityCode;
    private String referenceNumber;
    private String transactionCode;
    
    // Campos de conta de pagamento
    private String email;
    private String workPhoneCc;
    private String workPhoneSubscriber;
    private String mobilePhoneCc;
    private String mobilePhoneSubscriber;
    private String billAddrCity;
    private String billAddrCountry;
    private String billAddrLine1;
    private String billAddrLine2;
    private String billAddrLine3;
    private String billAddrPostCode;
    private String billAddrState;
}
