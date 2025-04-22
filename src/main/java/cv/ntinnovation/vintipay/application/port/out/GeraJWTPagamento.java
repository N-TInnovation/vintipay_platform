package cv.ntinnovation.vintipay.application.port.out;


import cv.ntinnovation.vintipay.application.core.domain.entity.Pagamento;

public interface GeraJWTPagamento {
	public String codificarPagamento(Pagamento pagamento) throws Exception;
	public Pagamento decodificarPagamento(String pagamento) throws Exception; 
}
