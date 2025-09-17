package cv.ntinnovation.vintipay.adapter.in.web.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para receber dados de desconto por ciclo em requisições
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DescontoCicloRequest {
    
    private Long id;
    
    @NotNull(message = "Número do ciclo é obrigatório")
    @Min(value = 1, message = "O ciclo deve ser maior ou igual a 1")
    private Integer numeroCiclo;
    
    @NotNull(message = "Percentual de desconto é obrigatório")
    @DecimalMin(value = "0.0", inclusive = true, message = "O percentual de desconto deve ser maior ou igual a 0")
    @DecimalMax(value = "100.0", inclusive = true, message = "O percentual de desconto deve ser menor ou igual a 100")
    private BigDecimal percentualDesconto;
    
    private String descricao;
}
