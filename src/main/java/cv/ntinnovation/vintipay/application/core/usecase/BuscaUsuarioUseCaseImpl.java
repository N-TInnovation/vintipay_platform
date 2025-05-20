package cv.ntinnovation.vintipay.application.core.usecase;

import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.port.in.BuscaUsuarioUseCase;
import cv.ntinnovation.vintipay.application.port.out.BuscaUsuarioPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BuscaUsuarioUseCaseImpl implements BuscaUsuarioUseCase {

    private final BuscaUsuarioPort buscaUsuarioPort;

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorEmail(String email) {
        log.info("Buscando usuário por e-mail: {}", email);
        return buscaUsuarioPort.buscarPorEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorResetToken(String token) {
        log.info("Buscando usuário por token de recuperação de senha");
        return buscaUsuarioPort.buscarPorResetToken(token);
    }
}
