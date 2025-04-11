package cv.ntinnovation.gateway_payment.adapter.in.web.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ConfiguracaoPagamentoResponse {
    private Long id;
    private String posId;
    private String merchantId;
    private String terminalId;
    private String apiKey;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
