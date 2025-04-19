package cv.ntinnovation.gateway_payment.application.core.usecase;

import cv.ntinnovation.gateway_payment.application.core.domain.entity.Usuario;
import cv.ntinnovation.gateway_payment.application.port.in.RegistroUsuarioUseCase;
import cv.ntinnovation.gateway_payment.application.port.out.SalvaUsuario;
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
