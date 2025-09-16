package cv.ntinnovation.vintipay.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "planos_assinatura")
@Builder
public class PlanoAssinaturaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioEntity usuario;
    
    @ManyToOne
    @JoinColumn(name = "plano_id", nullable = false)
    private PlanoEntity plano;
    
    @Column(name = "data_inicio")
    private LocalDateTime dataInicio;
    
    @Column(name = "data_fim")
    private LocalDateTime dataFim;
    
    @Column(name = "stripe_subscription_id")
    private String stripeSubscriptionId;
    
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private StatusAssinatura status;
    
    @Column(name = "ultimo_pagamento")
    private LocalDateTime ultimoPagamento;
    
    @Column(name = "proximo_pagamento")
    private LocalDateTime proximoPagamento;
    
    @Column(name = "valor_pago")
    private BigDecimal valorPago;
    
    public enum StatusAssinatura {
        ATIVA, TRIAL, CANCELADA, PENDENTE, EXPIRADA
    }
}
