package cv.ntinnovation.gateway_payment.application.port.out.db;

public interface PrecoDbGateway<T> {
    public void save(T price);
}
