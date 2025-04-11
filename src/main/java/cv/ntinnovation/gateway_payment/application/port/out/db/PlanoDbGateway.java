package cv.ntinnovation.gateway_payment.application.port.out.db;

import java.util.List;

public interface PlanoDbGateway<T> {
    public void save(T plano);
    public List<T> findAll();
}
