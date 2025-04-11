package cv.ntinnovation.gateway_payment.application.port.out;

import cv.ntinnovation.gateway_payment.application.core.domain.entity.ContaPagamento;

public interface SalvaContaPagamento {
	public ContaPagamento salvar(ContaPagamento contaPagamento);
}
