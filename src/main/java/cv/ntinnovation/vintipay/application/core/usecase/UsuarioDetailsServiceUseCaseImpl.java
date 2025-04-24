package cv.ntinnovation.vintipay.application.core.usecase;

import cv.ntinnovation.vintipay.application.port.in.UsuarioDetailsServiceUseCase;
import cv.ntinnovation.vintipay.application.port.out.BuscaUsuarioPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioDetailsServiceUseCaseImpl implements UsuarioDetailsServiceUseCase {

    private final BuscaUsuarioPort buscaUsuarioPort;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("entrou aqui");
        var usuario = buscaUsuarioPort.getUser(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));

        // Retornar o próprio objeto Usuario, que já implementa UserDetails
        // e agora tem um método getAuthorities() atualizado para usar papéis e permissões
        return usuario;
    }
}
