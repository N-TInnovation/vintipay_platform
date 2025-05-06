package cv.ntinnovation.vintipay.adapter.out.client.api.vintipay.response;

import cv.ntinnovation.vintipay.application.core.domain.enums.StatusIntencaoPagamentoEnum;
import cv.ntinnovation.vintipay.application.core.domain.enums.StatusPagamentoEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IntencaoPagamentoResponse {
    private BigDecimal amount;
    private String currency;
    private String reference;
    private StatusIntencaoPagamentoEnum status;
    private LocalDateTime createdAt;
    private String sessionId;
}