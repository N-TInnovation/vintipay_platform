package cv.ntinnovation.vintipay.adapter.out.persistence.entity;

import cv.ntinnovation.vintipay.application.core.domain.entity.ContaPagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.IntencaoPagamento;
import cv.ntinnovation.vintipay.application.core.domain.enums.AmbienteEnum;
import cv.ntinnovation.vintipay.application.core.domain.enums.StatusSubscricaoPagamentoEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "subscricao_pagamento")
@Getter
@Setter
public class SubscricaoPagamentoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "intencao_pagamento_id")
    private IntencaoPagamentoEntity intencaoPagamentoAtual;

    @Column(name = "reference", nullable = false, unique = true)
    private String reference;

    @Column(name = "fingerprint")
    private String fingerprint;

    @Column(name = "status")
    private StatusSubscricaoPagamentoEnum status;

    @Column(name = "pan_mascarado")
    private String panMascarado;

    @Column(name = "token_pagamento")
    private String tokenPagamento;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "result_finger_print")
    private String resultFingerPrint;

    @ManyToOne
    @JoinColumn(name = "conta_pagamento_id")
    private ContaPagamentoEntity contaPagamento;

    @Column(name = "ciclo")
    private Integer ciclo;

    @Column(name = "ambiente")
    private AmbienteEnum ambiente;
}
