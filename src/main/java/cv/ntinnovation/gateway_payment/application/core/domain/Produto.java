package cv.ntinnovation.gateway_payment.application.core.domain;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class Produto {
    private String name;
    private BigDecimal price;
}
