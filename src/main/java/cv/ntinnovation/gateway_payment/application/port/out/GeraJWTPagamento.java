package cv.ntinnovation.gateway_payment.application.port.out;


import cv.ntinnovation.gateway_payment.application.core.domain.entity.Pagamento;

public interface GeraJWTPagamento {
	public String codificarPagamento(Pagamento pagamento) throws Exception;
	public Pagamento decodificarPagamento(String pagamento) throws Exception; 
}
