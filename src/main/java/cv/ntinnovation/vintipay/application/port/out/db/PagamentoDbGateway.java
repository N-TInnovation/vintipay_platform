package cv.ntinnovation.vintipay.application.port.out.db;

public interface PagamentoDbGateway<T> {
    public void initSave(T payment);
}
