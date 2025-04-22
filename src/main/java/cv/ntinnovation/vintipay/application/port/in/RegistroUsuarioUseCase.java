package cv.ntinnovation.vintipay.application.port.in;

public interface RegistroUsuarioUseCase {
    void executar(String username, String password, String roles);
    
    void registrarNovoUsuario(String username, String email, String nome, String telefone, String password, String roles);
}
