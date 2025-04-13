package cv.ntinnovation.gateway_payment.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracaoPagamentoRequest {
    
    private Long id;
    
    @NotBlank(message = "O POS ID é obrigatório")
    private String posId;

    @NotBlank(message = "O Merchant ID é obrigatório")
    private String merchantId;

    @NotBlank(message = "O Terminal ID é obrigatório")
    private String terminalId;

    @NotBlank(message = "O POS Aut Code é obrigatório")
    private String posAutCode;

    @NotBlank(message = "A URL de retorno é obrigatória")
    private String returnUrl;

    private Boolean isActive;
}
