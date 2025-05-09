package cv.ntinnovation.vintipay;

import cv.ntinnovation.vintipay.adapter.out.db.repository.ConfiguracaoPagamentoRepository;
import cv.ntinnovation.vintipay.application.port.in.BuscaConfiguracaoPagamentoUseCase;
import cv.ntinnovation.vintipay.application.port.in.CriaConfiguracaoPagamentoUseCase;
import cv.ntinnovation.vintipay.application.port.in.CriaMetodoPagamentoUseCase;
import cv.ntinnovation.vintipay.application.port.out.BuscaConfiguracaoPagamento;
import cv.ntinnovation.vintipay.application.port.out.BuscaUsuario;
import cv.ntinnovation.vintipay.application.port.in.RegistroUsuarioUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultUserInitializer implements CommandLineRunner {

    private final RegistroUsuarioUseCase registroUsuarioUseCase;
    private final BuscaUsuario buscaUsuario;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        String username = buscaUsuario.getusername("admin");
        if (username.isEmpty()) {
            registroUsuarioUseCase.executar("admin", passwordEncoder.encode("admin123"), "ADMIN");
            log.info("Usuário padrão 'admin' criado com sucesso!");
        } else {
            log.info("Usuário padrão 'admin' já existe.");
        }
    }
}
