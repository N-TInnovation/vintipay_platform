package cv.ntinnovation.vintipay.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MetodoPagamentoRequest {
    @NotBlank(message = "nome é obrigatório")
    private String name;
}
