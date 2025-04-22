package cv.ntinnovation.vintipay.application.port.out.db;

public interface PrecoDbGateway<T> {
    public void save(T price);
}
