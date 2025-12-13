package cv.ntinnovation.vintipay.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Period;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "planos")
@Builder
public class PlanoEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String nome;
    
    @Column(length = 1000)
    private String descricao;
    
    @Column(nullable = false)
    private BigDecimal preco;
    
    @Column(name = "periodo_cobranca")
    @Enumerated(EnumType.STRING)
    private PeriodoCobranca periodoCobranca;
    
    @Column(name = "dias_trial")
    private Integer diasTrial;
    
    @Column(name = "stripe_price_id")
    private String stripePriceId;
    
    @Column(name = "stripe_product_id")
    private String stripeProductId;
    
    private boolean ativo = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private TenantEntity tenant;
    
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
    }
}
