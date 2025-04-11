package cv.ntinnovation.gateway_payment.application.core.domain.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Usuario {
    private Long id;
    private String username;
    private String password;
}
