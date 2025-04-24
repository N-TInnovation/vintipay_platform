package cv.ntinnovation.vintipay.application.core.usecase;

import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.port.in.GerenciaAssinaturaUseCase;
import cv.ntinnovation.vintipay.application.port.in.RegistroUsuarioUseCase;
import cv.ntinnovation.vintipay.application.port.out.BuscaUsuarioPort;
import cv.ntinnovation.vintipay.application.port.out.SalvaUsuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class RegistroUsuarioUseCaseImpl implements RegistroUsuarioUseCase {
    private final SalvaUsuario salvaUsuario;
    private final PasswordEncoder passwordEncoder;
    private final GerenciaAssinaturaUseCase gerenciaAssinaturaUseCase;
    private final BuscaUsuarioPort buscaUsuarioPort;
    
    @Override
    public void executar(String username, String password, String roles) {
        // Este método é usado pelo DefaultUserInitializer e já recebe a senha criptografada
        log.debug("Registrando usuário: {}", username);
        Usuario user = Usuario.builder(username, password, true, roles.split(","));
        salvaUsuario.salvar(user);
    }
    
    @Override
    @Transactional
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
    
    @Override
    @Transactional
    public void registrarNovoUsuarioComPlano(String username, String email, String nome, String telefone, String password, String roles, Long planoId) {
        log.debug("Registrando novo usuário com plano: {}, planoId: {}", username, planoId);
        // Criptografar a senha antes de salvar
        String senhaCriptografada = passwordEncoder.encode(password);
        
        // Criar o usuário
        Usuario user = Usuario.builder(username, email, nome, senhaCriptografada, true, roles.split(","));
        user.setTelefone(telefone);
        user.setDataRegistro(LocalDateTime.now());
        
        // Salvar o usuário
        salvaUsuario.salvar(user);
        log.info("Usuário {} registrado com sucesso", username);
        
        // Criar assinatura em uma nova transação
        criarAssinaturaParaUsuario(user, planoId);
    }
    
    @Override
    @Transactional
    public Usuario registrarNovoUsuarioSemPlano(String username, String email, String nome, String telefone, String password, String roles) {
        log.debug("Registrando novo usuário sem plano: {}", username);
        // Criptografar a senha antes de salvar
        String senhaCriptografada = passwordEncoder.encode(password);
        
        // Criar o usuário
        Usuario user = Usuario.builder(username, email, nome, senhaCriptografada, true, roles.split(","));
        user.setTelefone(telefone);
        user.setDataRegistro(LocalDateTime.now());
        
        // Salvar o usuário
        salvaUsuario.salvar(user);
        log.info("Usuário {} registrado com sucesso", username);
        
        return user;
    }
    
    @Override
    @Transactional
    public boolean criarAssinaturaAposRegistro(String username, Long planoId) {
        log.debug("Criando assinatura após registro para usuário: {}, planoId: {}", username, planoId);
        
        try {
            // Buscar o usuário pelo username
            Optional<Usuario> usuarioOptional = buscaUsuarioPort.buscarPorUsername(username);
            if (usuarioOptional.isEmpty()) {
                log.error("Usuário não encontrado com o username: {}", username);
                return false;
            }
            
            Usuario usuario = usuarioOptional.get();
            
            // Criar assinatura para o usuário
            return gerenciaAssinaturaUseCase.criarNovaAssinatura(usuario, planoId);
        } catch (Exception e) {
            log.error("Erro ao criar assinatura após registro: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void criarAssinaturaParaUsuario(Usuario usuario, Long planoId) {
        try {
            // Criar assinatura para o usuário com o plano escolhido
            boolean assinaturaSuccess = gerenciaAssinaturaUseCase.criarNovaAssinatura(usuario, planoId);
            if (assinaturaSuccess) {
                log.info("Assinatura criada com sucesso para o usuário {} com o plano {}", usuario.getUsername(), planoId);
            } else {
                log.warn("Não foi possível criar a assinatura para o usuário {} com o plano {}", usuario.getUsername(), planoId);
            }
        } catch (Exception e) {
            log.error("Erro ao criar assinatura para o usuário {} com o plano {}: {}", usuario.getUsername(), planoId, e.getMessage(), e);
            // Não lançamos a exceção para não impedir o registro do usuário
        }
    }
}
