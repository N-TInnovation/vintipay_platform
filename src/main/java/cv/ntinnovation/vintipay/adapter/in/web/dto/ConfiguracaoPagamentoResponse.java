package cv.ntinnovation.vintipay.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracaoPagamentoResponse {
    private Long id;
    private String posId;
    private String merchantId;
    private String terminalId;
    private String posAutCode;
    private String returnAutCode;
    private String returnUrl;
    private Boolean isActive;
    private String nomeLoja;
    private String urlLoja;
    private String telefone;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
