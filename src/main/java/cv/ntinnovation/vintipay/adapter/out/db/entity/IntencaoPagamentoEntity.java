package cv.ntinnovation.vintipay.adapter.out.db.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "intencao_pagamento")
@Getter
@Setter
public class IntencaoPagamentoEntity extends BasePagamentoEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "conta_pagamento_id", nullable = false)
    private ContaPagamentoEntity contaPagamento;

    @Column(name = "merchant_ref", nullable = false)
    private String merchantRef;

    @Column(name = "merchant_session", nullable = false)
    private String merchantSession;

    @Column(name = "pos_id", nullable = false)
    private String posId;

    @Column(name = "timestamp", nullable = false)
    private String timestamp;
    
    @Column(name = "entity_code", nullable = true)
    private String entityCode;
    
    @Column(name = "reference_number", nullable = true)
    private Long referenceNumber;
}
