package cv.ntinnovation.vintipay.adapter.out.persistence.entity;

import cv.ntinnovation.vintipay.application.core.domain.enums.IntencaoPagamentoEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;

@Entity
@Table(name = "intencao_pagamento")
@Getter
@Setter
public class IntencaoPagamentoEntity extends BasePagamentoEntity {

    @Column(name = "intencao", nullable = false)
    @Enumerated(EnumType.STRING)
    private IntencaoPagamentoEnum intencao;

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
    
    @Column(name = "entity_code")
    private String entityCode;
    
    @Column(name = "reference_number")
    private Long referenceNumber;

    @Column(name = "ambiente", nullable = false)
    private String ambiente;

    @Column(name = "recorrente", nullable = false)
    private boolean recorrente;

    @Column(name = "data_cobranca")
    private LocalDate dataCobranca;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "produto_id", nullable = false)
    private ProdutoEntity produto;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subscricao_pagamento_id")
    private SubscricaoPagamentoEntity subscricaoPagamento;

    @Column(name = "ciclo")
    private Integer ciclo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private TenantEntity tenant;
}
