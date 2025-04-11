package cv.ntinnovation.gateway_payment.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutResponse {
    private BigDecimal amount;
    private String currency;
    private String fingerprint;
    private String timestamp;
    private String merchantRef;
    private String merchantSession;
    private String posId;
    private String transactionCode;
    private String entityCode;
    private String referenceNumber;
}
