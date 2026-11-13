package cv.ntinnovation.vintipay.application.core.usecase;

import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.port.in.GerenciaUsuarioUseCase;
import cv.ntinnovation.vintipay.application.port.out.BuscaUsuarioPort;
import cv.ntinnovation.vintipay.application.port.out.SalvaUsuarioPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GerenciaUsuarioUseCaseImpl implements GerenciaUsuarioUseCase {

    private final BuscaUsuarioPort buscaUsuarioPort;
    private final SalvaUsuarioPort salvaUsuarioPort;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Usuario criarUsuario(Usuario usuario) {
        log.info("Criando novo usuário: {}", usuario.getUsername());

        if (buscaUsuarioPort.buscarPorUsername(usuario.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Nome de usuário já existe");
        }

        if (usuario.getEmail() != null && !usuario.getEmail().isEmpty() &&
            buscaUsuarioPort.buscarPorEmail(usuario.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email já está em uso");
        }

        if (!usuario.getPassword().startsWith("$2a$")) {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }

        if (usuario.getDataRegistro() == null) {
            usuario.setDataRegistro(LocalDateTime.now());
        }

        return salvaUsuarioPort.salvar(usuario);
    }

    @Override
    @Transactional
    public Usuario atualizarUsuario(Usuario usuario) {
        log.info("Atualizando usuário ID: {}", usuario.getId());

        Usuario existing = buscaUsuarioPort.buscarPorId(usuario.getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        if (usuario.getUsername() != null && !existing.getUsername().equals(usuario.getUsername()) &&
            buscaUsuarioPort.buscarPorUsername(usuario.getUsername())
                    .filter(u -> !u.getId().equals(usuario.getId())).isPresent()) {
            throw new IllegalArgumentException("Nome de usuário já existe");
        }

        if (usuario.getEmail() != null && !usuario.getEmail().isEmpty() &&
            (existing.getEmail() == null || !existing.getEmail().equals(usuario.getEmail())) &&
            buscaUsuarioPort.buscarPorEmail(usuario.getEmail())
                    .filter(u -> !u.getId().equals(usuario.getId())).isPresent()) {
            throw new IllegalArgumentException("Email já está em uso por outro usuário");
        }

        if (usuario.getPassword() == null || usuario.getPassword().isEmpty()) {
            usuario.setPassword(existing.getPassword());
        } else if (!usuario.getPassword().startsWith("$2a$")) {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }

        if (usuario.getRole() == null || usuario.getRole().length == 0) {
            usuario.setRole(new String[]{"USER"});
        }

        return salvaUsuarioPort.salvar(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorId(Long id) {
        log.info("Buscando usuário por ID: {}", id);
        return buscaUsuarioPort.buscarPorId(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorUsername(String username) {
        log.info("Buscando usuário por username: {}", username);
        return buscaUsuarioPort.buscarPorUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorEmail(String email) {
        log.info("Buscando usuário por email: {}", email);
        return buscaUsuarioPort.buscarPorEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        log.info("Listando todos os usuários");
        return buscaUsuarioPort.listarTodos();
    }

    @Override
    @Transactional
    public Usuario ativarUsuario(Long id) {
        log.info("Ativando usuário ID: {}", id);
        Usuario usuario = buscaUsuarioPort.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        usuario.setAtivo(true);
        return salvaUsuarioPort.salvar(usuario);
    }

    @Override
    @Transactional
    public Usuario desativarUsuario(Long id) {
        log.info("Desativando usuário ID: {}", id);
        Usuario usuario = buscaUsuarioPort.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        usuario.setAtivo(false);
        return salvaUsuarioPort.salvar(usuario);
    }

    @Override
    @Transactional
    public Usuario alterarSenha(Long id, String senhaCriptografada) {
        log.info("Alterando senha do usuário ID: {}", id);
        Usuario usuario = buscaUsuarioPort.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        usuario.setPassword(senhaCriptografada);
        return salvaUsuarioPort.salvar(usuario);
    }
}
