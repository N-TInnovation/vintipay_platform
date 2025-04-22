package cv.ntinnovation.vintipay.application.port.in;

public interface RegistroUsuarioUseCase {
    public void executar(String Username, String password, String role);
}
