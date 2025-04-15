package cv.ntinnovation.gateway_payment.application.core.domain.entity;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Usuario {
    private Long id;
    private String username;
    private String password;
    private String role;
    private boolean ativo;
}
