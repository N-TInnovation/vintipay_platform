package cv.ntinnovation.gateway_payment.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MetodoPagamentoRequest {
    @NotBlank(message = "nome é obrigatório")
    private String name;
}
