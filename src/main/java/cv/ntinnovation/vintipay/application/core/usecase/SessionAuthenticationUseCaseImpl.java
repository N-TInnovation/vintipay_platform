package cv.ntinnovation.vintipay.application.core.usecase;

import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.port.in.SessionAuthenticationUseCase;
import cv.ntinnovation.vintipay.application.port.out.BuscaUsuario;
import cv.ntinnovation.vintipay.application.port.out.SalvaUsuarioPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class SessionAuthenticationUseCaseImpl implements SessionAuthenticationUseCase {

    private final BuscaUsuario usuarioUseCase;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.debug("Autenticando usuário: {}", authentication.getName());
        
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        UserDetails userDetails = usuarioUseCase.getUser(username);

        // Verificar se a senha está correta
        boolean senhaCorreta = passwordEncoder.matches(password, userDetails.getPassword());

        if (!senhaCorreta) {
            log.warn("Falha na autenticação para o usuário: {} - Senha incorreta", username);
            throw new BadCredentialsException("Senha incorreta");
        }

        // Atualizar o último login do usuário
        if (userDetails instanceof Usuario) {
            Usuario usuario = (Usuario) userDetails;
            usuario.setUltimoLogin(LocalDateTime.now());

            // Verificar se o email foi confirmado
            if (!usuario.isEmailVerified()) {
                log.warn("Tentativa de login com email não verificado: {}", username);
                throw new DisabledException("Email não verificado. Por favor, verifique seu email para ativar sua conta.");
            }

            // Salvar o usuário atualizado
            if (usuarioUseCase instanceof SalvaUsuarioPort) {
                ((SalvaUsuarioPort) usuarioUseCase).salvar(usuario);
            } else {
                log.warn("Não foi possível salvar o último login do usuário: {} - Interface SalvaUsuario não implementada", username);
            }

            log.info("Último login atualizado para o usuário: {}", username);
        }

        log.info("Usuário {} autenticado com sucesso", username);

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null, // Limpar credenciais após autenticação bem-sucedida
                userDetails.getAuthorities()
        );

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
