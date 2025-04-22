package cv.ntinnovation.vintipay.application.core.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TipoPagamentoEanum {
	RECARGA(3,"Recarga"),
	PAGAMENTO_SERVICO(2,"Pagamento de Serviço"),
	COMPRA(1,"Compras");
	private int code;
	private String descricao;
}
