package cv.ntinnovation.vintipay.application.core.usecase;

import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.port.in.RegistroUsuarioUseCase;
import cv.ntinnovation.vintipay.application.port.out.SalvaUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RegistroUsuarioUseCaseImpl implements RegistroUsuarioUseCase {
    private final SalvaUsuario salvaUsuario;
    @Override
    public void executar(String username, String password, String roles) {

        Usuario user = Usuario.builder(username, password, true, roles.split(","));
        salvaUsuario.salvar(user);

    }
}
