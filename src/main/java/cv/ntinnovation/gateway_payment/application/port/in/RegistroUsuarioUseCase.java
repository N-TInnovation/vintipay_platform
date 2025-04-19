package cv.ntinnovation.gateway_payment.application.port.in;

public interface RegistroUsuarioUseCase {
    public void executar(String Username, String password, String role);
}
