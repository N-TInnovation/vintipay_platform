package cv.ntinnovation.gateway_payment.adapter.out.db.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "metodo_pagamento")
@Data
public class MetodoPagamentoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", unique = true)
    private String name;
}
