package cv.ntinnovation.vintipay.application.core.usecase;

import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.port.in.RegistroUsuarioUseCase;
import cv.ntinnovation.vintipay.application.port.out.SalvaUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class RegistroUsuarioUseCaseImpl implements RegistroUsuarioUseCase {
    private final SalvaUsuario salvaUsuario;
    
    @Override
    public void executar(String username, String password, String roles) {
        Usuario user = Usuario.builder(username, password, true, roles.split(","));
        salvaUsuario.salvar(user);
    }
    
    @Override
    public void registrarNovoUsuario(String username, String email, String nome, String telefone, String password, String roles) {
        Usuario user = Usuario.builder(username, email, nome, password, true, roles.split(","));
        user.setTelefone(telefone);
        user.setDataRegistro(LocalDateTime.now());
        salvaUsuario.salvar(user);
    }
}
