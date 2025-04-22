package cv.ntinnovation.vintipay.adapter.out.db.entity;

import cv.ntinnovation.vintipay.application.core.domain.enums.StatusPagamentoEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
public abstract class BasePagamentoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusPagamentoEnum status;

    @Column(name = "reference", unique = true)
    private String reference;
    
    @Column(name = "currency")
    private String currency;
    
    @Column(name = "transaction_code")
    private String transactionCode;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
