package cv.ntinnovation.gateway_payment.application.port.in;

public interface ExecutaFinalizaPagamentoUseCase<T> {
	public void executar(T finalizaPagamentoSisp);
}
