package cv.ntinnovation.gateway_payment.application.port.out.db;

public interface PagamentoDbGateway<T> {
    public void initSave(T payment);
}
