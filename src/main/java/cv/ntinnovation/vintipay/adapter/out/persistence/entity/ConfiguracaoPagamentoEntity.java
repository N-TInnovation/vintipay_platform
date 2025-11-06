package cv.ntinnovation.vintipay.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "configuracao_pagamento",
       uniqueConstraints = {
           @UniqueConstraint(name = "UK_POS_ID", columnNames = "pos_id")
       })
@Getter
@Setter
@ToString(exclude = {"usuario"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracaoPagamentoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pos_id", nullable = false)
    private String posId;

    @Column(name = "merchant_id", nullable = false)
    private String merchantId;

    @Column(name = "api_key", nullable = false, unique = true)
    private String apiKey;

    @Column(name = "api_secret", nullable = false)
    private String apiSecret;

    @Column(name = "pos_aut_code", nullable = false)
    private String posAutCode;

    @Column(name = "return_aut_code", nullable = false)
    private String returnAutCode;

    @Column(name = "return_url", nullable = false)
    private String returnUrl;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "nome_loja", nullable = false)
    private String nomeLoja;
    
    @Column(name = "url_loja", nullable = false)
    private String urlLoja;
    
    @Column(name = "telefone", nullable = false)
    private String telefone;
    
    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "ambiente", nullable = false)
    private String ambiente;

    @Column(name = "webhook_url")
    private String webhookUrl;
    
    @Column(name = "webhook_secret", nullable = false)
    private String webhookSecret;

    @Column(name = "webhook_ativo")
    private Boolean webhookAtivo;

    @Column(name = "return_ativo")
    private Boolean returnAtivo;

    @Column(name = "ativo")
    private Boolean ativo;
    
    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private UsuarioEntity usuario;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfiguracaoPagamentoEntity that = (ConfiguracaoPagamentoEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
