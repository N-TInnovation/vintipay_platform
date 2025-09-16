package cv.ntinnovation.vintipay.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "contato_conta_pagamento")
@Getter
@Setter
public class ContatoContaPagamentoEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "cc")
	private String cc; 
	
	@Column(name = "subscriber")
	private String subscriber;
}
