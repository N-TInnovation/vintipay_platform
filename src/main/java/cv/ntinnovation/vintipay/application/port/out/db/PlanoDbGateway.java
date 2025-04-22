package cv.ntinnovation.vintipay.application.port.out.db;

import java.util.List;

public interface PlanoDbGateway<T> {
    public void save(T plano);
    public List<T> findAll();
}
