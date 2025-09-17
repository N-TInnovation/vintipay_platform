package cv.ntinnovation.vintipay.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para enviar dados de desconto por ciclo em respostas
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DescontoCicloResponse {
    
    private Long id;
    private Integer numeroCiclo;
    private BigDecimal percentualDesconto;
    private String descricao;
    private Long produtoId;
}
