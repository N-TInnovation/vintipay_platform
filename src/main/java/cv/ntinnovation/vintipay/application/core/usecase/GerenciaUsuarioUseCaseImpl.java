package cv.ntinnovation.vintipay.application.core.usecase;

import cv.ntinnovation.vintipay.adapter.out.db.entity.UsuarioEntity;
import cv.ntinnovation.vintipay.adapter.out.db.mapper.UsusarioMapper;
import cv.ntinnovation.vintipay.adapter.out.db.repository.UsuarioRepository;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.port.in.GerenciaUsuarioUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GerenciaUsuarioUseCaseImpl implements GerenciaUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;
    private final UsusarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Usuario criarUsuario(Usuario usuario) {
        log.info("Criando novo usuário: {}", usuario.getUsername());
        
        // Verifica se o username já existe
        if (usuarioRepository.findByUsername(usuario.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Nome de usuário já existe");
        }
        
        // Verifica se o email já existe
        if (usuario.getEmail() != null && !usuario.getEmail().isEmpty() && 
            usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email já está em uso");
        }
        
        // Criptografa a senha se não estiver criptografada
        if (!usuario.getPassword().startsWith("$2a$")) {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }
        
        // Define a data de registro se não estiver definida
        if (usuario.getDataRegistro() == null) {
            usuario.setDataRegistro(LocalDateTime.now());
        }
        
        // Converte para entidade, salva e converte de volta para domínio
        UsuarioEntity usuarioEntity = usuarioMapper.toEntity(usuario);
        UsuarioEntity savedEntity = usuarioRepository.save(usuarioEntity);
        return usuarioMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional
    public Usuario atualizarUsuario(Usuario usuario) {
        log.info("Atualizando usuário ID: {}", usuario.getId());
        
        // Verifica se o usuário existe
        UsuarioEntity existingEntity = usuarioRepository.findById(usuario.getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        
        // Verifica se o username foi alterado e se já existe
        if (usuario.getUsername() != null && !existingEntity.getUsername().equals(usuario.getUsername())) {
            Optional<UsuarioEntity> existingUsername = usuarioRepository.findByUsername(usuario.getUsername());
            if (existingUsername.isPresent() && !existingUsername.get().getId().equals(usuario.getId())) {
                throw new IllegalArgumentException("Nome de usuário já existe");
            }
        }
        
        // Verifica se o email foi alterado e se já existe
        if (usuario.getEmail() != null && !usuario.getEmail().isEmpty()) {
            // Se o email atual é diferente do email existente (ou se o existente é nulo)
            if (existingEntity.getEmail() == null || !existingEntity.getEmail().equals(usuario.getEmail())) {
                // Verifica se o novo email já existe para outro usuário
                Optional<UsuarioEntity> existingEmail = usuarioRepository.findByEmail(usuario.getEmail());
                if (existingEmail.isPresent() && !existingEmail.get().getId().equals(usuario.getId())) {
                    throw new IllegalArgumentException("Email já está em uso por outro usuário");
                }
            }
        }
        
        // Mantém a senha atual se não for fornecida uma nova
        if (usuario.getPassword() == null || usuario.getPassword().isEmpty()) {
            usuario.setPassword(existingEntity.getPassword());
        } else if (!usuario.getPassword().startsWith("$2a$")) {
            // Criptografa a senha se for nova e não estiver criptografada
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }
        
        // Garantir que o array de roles não seja nulo
        if (usuario.getRole() == null || usuario.getRole().length == 0) {
            usuario.setRole(new String[]{"USER"});
        }
        
        // Converte para entidade, salva e converte de volta para domínio
        UsuarioEntity usuarioEntity = usuarioMapper.toEntity(usuario);
        UsuarioEntity savedEntity = usuarioRepository.save(usuarioEntity);
        return usuarioMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorId(Long id) {
        log.info("Buscando usuário por ID: {}", id);
        return usuarioRepository.findById(id)
                .map(usuarioMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorUsername(String username) {
        log.info("Buscando usuário por username: {}", username);
        return usuarioRepository.findByUsername(username)
                .map(usuarioMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorEmail(String email) {
        log.info("Buscando usuário por email: {}", email);
        return usuarioRepository.findByEmail(email)
                .map(usuarioMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        log.info("Listando todos os usuários");
        return usuarioRepository.findAll().stream()
                .map(usuarioMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Usuario ativarUsuario(Long id) {
        log.info("Ativando usuário ID: {}", id);
        UsuarioEntity usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        
        usuario.setAtivo(true);
        UsuarioEntity savedEntity = usuarioRepository.save(usuario);
        return usuarioMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional
    public Usuario desativarUsuario(Long id) {
        log.info("Desativando usuário ID: {}", id);
        UsuarioEntity usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        
        usuario.setAtivo(false);
        UsuarioEntity savedEntity = usuarioRepository.save(usuario);
        return usuarioMapper.toDomain(savedEntity);
    }
}
