package cv.ntinnovation.vintipay.application.port.out.db;

public interface ProdutoDbGateway<T> {
    public void save(T produto);
}
