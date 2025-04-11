package cv.ntinnovation.gateway_payment.application.core.domain.entity;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class DadoPagamento {
	private String campo;
	private String valor;
}
