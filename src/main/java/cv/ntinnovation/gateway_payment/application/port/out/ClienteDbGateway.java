package cv.ntinnovation.gateway_payment.application.port.out;

public interface ClienteDbGateway<T>{
    public void saveCustomer(T customer);
}
