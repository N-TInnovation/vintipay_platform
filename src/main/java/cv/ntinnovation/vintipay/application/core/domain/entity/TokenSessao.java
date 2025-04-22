package cv.ntinnovation.vintipay.application.core.domain.entity;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.Instant;

@Data
@Builder
@ToString
public class TokenSessao {
    private Long id;
    private Usuario usuario;
    private String sessionCode;
    private Instant expirationTime;
}
