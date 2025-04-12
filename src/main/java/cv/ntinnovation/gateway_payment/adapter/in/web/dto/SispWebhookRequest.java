package cv.ntinnovation.gateway_payment.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SispWebhookRequest {
    private String merchantRef;
    private String merchantSession;
    private String transactionCode;
    private String posID;
    private String amount;
    private String currency;
    private String timestamp;
    private String status;
    private String message;
    private String authorizationCode;
    private String pan; // Número do cartão mascarado
    private String fingerprint;
    private String fingerprintversion;
}
