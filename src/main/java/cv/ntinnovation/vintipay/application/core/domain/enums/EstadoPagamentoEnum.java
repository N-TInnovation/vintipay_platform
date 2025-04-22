package cv.ntinnovation.vintipay.application.core.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EstadoPagamentoEnum {
	PEDIDO_PAGAMENTO_FEITO(0),
	AGUARDANDO_RETONO_PAGAMENTO_SISP(1),
	RETONO_PAGAMENTO_SISP_SUCESSO(2),
	RETONO_PAGAMENTO_SISP_FALHO(3),
	PEDIDO_PAGAMENTO_FALHO(4);
	
	private int code;
}
