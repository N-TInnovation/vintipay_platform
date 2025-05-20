package cv.ntinnovation.vintipay.application.core.usecase;

import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.port.in.AtualizaUsuarioUseCase;
import cv.ntinnovation.vintipay.application.port.out.SalvaUsuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AtualizaUsuarioUseCaseImpl implements AtualizaUsuarioUseCase {

    private final SalvaUsuario salvaUsuario;

    @Override
    @Transactional
    public Usuario atualizar(Usuario usuario) {
        log.info("Atualizando usuário: {}", usuario.getUsername());
        return salvaUsuario.salvar(usuario);
    }
}
