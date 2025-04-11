package cv.ntinnovation.gateway_payment.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ConfiguracaoPagamentoRequest {
    @NotBlank(message = "posId é obrigatório")
    private String posId;

    @NotBlank(message = "merchantId é obrigatório")
    private String merchantId;

    @NotBlank(message = "terminalId é obrigatório")
    private String terminalId;

    @NotBlank(message = "apiKey é obrigatório")
    private String apiKey;

    @NotBlank(message = "apiSecret é obrigatório")
    private String apiSecret;
}
