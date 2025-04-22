package cv.ntinnovation.vintipay.adapter.in.web.dto;

import cv.ntinnovation.vintipay.application.core.domain.enums.StatusPagamentoEnum;
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
public class IntencaoPagamentoResponse {
    private BigDecimal amount;
    private String currency;
    private String reference;
    private StatusPagamentoEnum status;
    private LocalDateTime createdAt;
    private String sessionId;
}
