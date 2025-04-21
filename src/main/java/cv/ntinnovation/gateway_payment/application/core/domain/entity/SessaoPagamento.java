package cv.ntinnovation.gateway_payment.application.core.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessaoPagamento {
    private Long id;
    private String sessionId;
    private String reference;
    private String clientId;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private boolean valid;
}
