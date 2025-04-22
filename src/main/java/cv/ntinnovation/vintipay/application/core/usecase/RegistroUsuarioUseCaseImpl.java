package cv.ntinnovation.vintipay.application.core.usecase;

import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.port.in.RegistroUsuarioUseCase;
import cv.ntinnovation.vintipay.application.port.out.SalvaUsuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class RegistroUsuarioUseCaseImpl implements RegistroUsuarioUseCase {
    private final SalvaUsuario salvaUsuario;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void executar(String username, String password, String roles) {
        // Este método é usado pelo DefaultUserInitializer e já recebe a senha criptografada
        log.debug("Registrando usuário: {}", username);
        Usuario user = Usuario.builder(username, password, true, roles.split(","));
        salvaUsuario.salvar(user);
    }
    
    @Override
    public void registrarNovoUsuario(String username, String email, String nome, String telefone, String password, String roles) {
        log.debug("Registrando novo usuário: {}", username);
        // Criptografar a senha antes de salvar
        String senhaCriptografada = passwordEncoder.encode(password);
        
        Usuario user = Usuario.builder(username, email, nome, senhaCriptografada, true, roles.split(","));
        user.setTelefone(telefone);
        user.setDataRegistro(LocalDateTime.now());
        salvaUsuario.salvar(user);
        log.info("Usuário {} registrado com sucesso", username);
    }
}
