package cv.ntinnovation.vintipay.application.port.out;

public interface ClienteDbGateway<T>{
    public void saveCustomer(T customer);
}
