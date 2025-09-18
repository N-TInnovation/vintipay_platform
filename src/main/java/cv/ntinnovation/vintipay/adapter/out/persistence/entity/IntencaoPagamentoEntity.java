package cv.ntinnovation.vintipay.adapter.out.persistence.entity;

import cv.ntinnovation.vintipay.application.core.domain.enums.IntencaoPagamentoEnum;
import cv.ntinnovation.vintipay.application.core.domain.enums.PrimeiraCobrancaEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

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

    @Column(name = "ciclo")
    private Integer ciclo;

    @Column(name = "intencao", nullable = false)
    private IntencaoPagamentoEnum intencao;

    @Column(name = "primeira_cobranca")
    private PrimeiraCobrancaEnum primeiraCobranca;
}
