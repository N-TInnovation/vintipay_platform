package cv.ntinnovation.gateway_payment.application.port.in;


public interface ExecutaInicioPagamentoUseCase<T> {
	public void executar(T iniciaPagamento);
}
