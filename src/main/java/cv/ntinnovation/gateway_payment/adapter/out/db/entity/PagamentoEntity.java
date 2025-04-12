package cv.ntinnovation.gateway_payment.adapter.out.db.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "pagamento")
@Data
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

    @Column(name = "timestamp", nullable = false)
    private String timestamp;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "mensagem")
    private String mensagem;

    @Column(name = "codigo_autorizacao")
    private String codigoAutorizacao;

    @Column(name = "pan_mascarado")
    private String panMascarado;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
