package cv.ntinnovation.vintipay.application.core.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Representa um desconto aplicado em um ciclo específico de cobrança recorrente
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DescontoCiclo {
    
    private Long id;
    
    // Número do ciclo ao qual este desconto se aplica (1 = primeiro ciclo)
    private Integer numeroCiclo;
    
    // Valor do desconto em porcentagem (de 0 a 100)
    private BigDecimal percentualDesconto;
    
    // Descrição do desconto para exibição ao cliente
    private String descricao;
    
    // Referência ao produto associado
    private Long produtoId;
}
