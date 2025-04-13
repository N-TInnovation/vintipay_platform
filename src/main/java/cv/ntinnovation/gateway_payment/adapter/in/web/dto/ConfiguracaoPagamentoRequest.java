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

    @NotBlank(message = "A API Key é obrigatória")
    private String apiKey;

    @NotBlank(message = "O API Secret é obrigatório")
    private String apiSecret;

    @NotBlank(message = "O POS Aut Code é obrigatório")
    private String posAutCode;

    @NotNull(message = "O status ativo/inativo é obrigatório")
    private Boolean isActive;
}
