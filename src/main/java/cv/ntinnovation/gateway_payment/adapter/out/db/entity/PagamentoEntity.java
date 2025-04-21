package cv.ntinnovation.gateway_payment.adapter.out.db.entity;

import cv.ntinnovation.gateway_payment.application.core.domain.enums.StatusPagamentoEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "pagamento")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagamentoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "intencao_pagamento_id", nullable = false)
    private IntencaoPagamentoEntity intencaoPagamento;

    @Column(name = "fingerprint", nullable = false)
    private String fingerprint;

    @Column(name = "result_fingerprint")
    private String resultFingerprint;

    @Column(name = "timestamp", nullable = false)
    private String timestamp;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusPagamentoEnum status;

    @Column(name = "mensagem")
    private String mensagem;

    @Column(name = "reference", unique = true)
    private String reference;

    @Column(name = "pan_mascarado")
    private String panMascarado;

    @Column(name = "authorization_code", columnDefinition = "TEXT")
    private String authorizationCode;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
