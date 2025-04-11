package cv.ntinnovation.gateway_payment.adapter.in.web.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AutenticacaoResponse {
    private String accessToken;
    private String error;
}
