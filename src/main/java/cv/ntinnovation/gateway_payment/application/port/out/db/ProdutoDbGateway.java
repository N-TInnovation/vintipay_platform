package cv.ntinnovation.gateway_payment.application.port.out.db;

public interface ProdutoDbGateway<T> {
    public void save(T produto);
}
