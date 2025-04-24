package cv.ntinnovation.vintipay.application.core.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Period;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Plano {
    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private PeriodoCobranca periodoCobranca;
    private Integer diasTrial;
    private String stripePriceId;
    private String stripeProductId;
    private boolean ativo;
    
    public enum PeriodoCobranca {
        MENSAL(Period.ofMonths(1)),
        TRIMESTRAL(Period.ofMonths(3)),
        SEMESTRAL(Period.ofMonths(6)),
        ANUAL(Period.ofYears(1));
        
        private final Period periodo;
        
        PeriodoCobranca(Period periodo) {
            this.periodo = periodo;
        }
        
        public Period getPeriodo() {
            return periodo;
        }
        
        public String getDescricao() {
            return switch (this) {
                case MENSAL -> "Mensal";
                case TRIMESTRAL -> "Trimestral";
                case SEMESTRAL -> "Semestral";
                case ANUAL -> "Anual";
            };
        }
    }
}
