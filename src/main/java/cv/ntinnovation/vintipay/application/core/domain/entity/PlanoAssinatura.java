package cv.ntinnovation.vintipay.application.core.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanoAssinatura {
    private Long id;
    private Usuario usuario;
    private Plano plano;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private String stripeSubscriptionId;
    private StatusAssinatura status;
    private LocalDateTime ultimoPagamento;
    private LocalDateTime proximoPagamento;
    private BigDecimal valorPago;
    
    public enum StatusAssinatura {
        ATIVA, TRIAL, CANCELADA, PENDENTE, EXPIRADA
    }
}
