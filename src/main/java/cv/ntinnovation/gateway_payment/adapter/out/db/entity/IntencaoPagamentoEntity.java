package cv.ntinnovation.gateway_payment.adapter.out.db.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "intencao_pagamento")
@Getter
@Setter
public class IntencaoPagamentoEntity extends BasePagamentoEntity {

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "conta_pagamento_id", nullable = false)
    private ContaPagamentoEntity contaPagamento;

    @Column(name = "merchant_ref", nullable = true)
    private String merchantRef;

    @Column(name = "merchant_session", nullable = true)
    private String merchantSession;

    @Column(name = "pos_id", nullable = true)
    private String posId;

    @Column(name = "timestamp", nullable = true)
    private String timestamp;
    
    @Column(name = "entity_code", nullable = true)
    private String entityCode;
    
    @Column(name = "reference_number", nullable = true)
    private Long referenceNumber;
}
