package cv.ntinnovation.vintipay.application.core.usecase;

import cv.ntinnovation.vintipay.application.port.in.UsuarioDetailsServiceUseCase;
import cv.ntinnovation.vintipay.application.port.out.BuscaUsuarioPort;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioDetailsServiceUseCaseImpl implements UsuarioDetailsServiceUseCase {

    private final BuscaUsuarioPort buscaUsuarioPort;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Buscando usuário: {}", username);
        
        var usuario = buscaUsuarioPort.getUser(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));

        // Verificar se o email foi confirmado
        if (!usuario.isEmailVerified()) {
            log.warn("Tentativa de login com email não verificado: {}", username);
            throw new DisabledException("Email não verificado. Por favor, verifique seu email para ativar sua conta.");
        }

        // Retornar o próprio objeto Usuario, que já implementa UserDetails
        // e agora tem um método getAuthorities() atualizado para usar papéis e permissões
        return usuario;
    }
}
