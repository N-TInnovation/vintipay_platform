package cv.ntinnovation.vintipay.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "info_conta_pagamento")
@Getter
@Setter
public class InfoContaPagamentoEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "ch_acc_age_ind")
	private String chAccAgeInd;
	
	@Column(name = "ch_acc_change")
	private String chAccChange;
	
	@Column(name = "ch_acc_date")
	private String chAccDate;
	
	@Column(name = "ch_acc_pw_change")
	private String chAccPwChange;
	
	@Column(name = "ch_acc_pw_change_ind")
	private String chAccPwChangeInd;
	
	@Column(name = "suspicious_acc_activity")
	private String suspiciousAccActivity;
}
