package cv.ntinnovation.gateway_payment.adapter.in.web.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CheckoutResponseDto {
    private String reference;
    private String status;
    private String message;
    private BigDecimal amount;
    private String currency;
    private String merchantSession;
    private String merchantRef;
    private String timestamp;
    private String panMascarado;
    private String returnUrl;
}
