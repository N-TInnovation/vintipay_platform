package cv.ntinnovation.gateway_payment.adapter.in.web.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MetodoPagamentoResponse {
    private Long id;
    private String name;
}
