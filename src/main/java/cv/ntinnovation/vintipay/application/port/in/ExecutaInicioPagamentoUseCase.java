package cv.ntinnovation.vintipay.application.port.in;


public interface ExecutaInicioPagamentoUseCase<T> {
	public void executar(T iniciaPagamento);
}
