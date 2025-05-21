package cv.ntinnovation.vintipay.application.core.usecase;

import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.core.util.TokenUtil;
import cv.ntinnovation.vintipay.application.port.in.VerificaEmailUseCase;
import cv.ntinnovation.vintipay.application.port.out.BuscaUsuarioPort;
import cv.ntinnovation.vintipay.application.port.out.BrevoServicePort;
import cv.ntinnovation.vintipay.application.port.out.SalvaUsuarioPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificaEmailUseCaseImpl implements VerificaEmailUseCase {

    private final BuscaUsuarioPort buscaUsuarioPort;
    private final SalvaUsuarioPort salvaUsuarioPort;
    private final BrevoServicePort brevoServicePort;
    private final TemplateEngine emailTemplateEngine;
    
    @Value("${app.base-url:http://localhost:8080}")
    private String appUrl;
    
    @Value("${app.email.verification.expiry-hours:24}")
    private int emailTokenExpiryHours;

    @Override
    public boolean criarTokenVerificacaoEEnviarEmail(Usuario usuario) {
        if (usuario == null || usuario.getEmail() == null) {
            log.error("Usuário ou email inválido");
            return false;
        }
        
        try {
            // Buscar o usuário existente
            Optional<Usuario> usuarioExistente = buscaUsuarioPort.buscarPorUsername(usuario.getUsername());
            
            if(usuarioExistente.isEmpty()) {
                log.error("Usuário não encontrado para geração de token: {}", usuario.getUsername());
                return false;
            }
            
            Usuario usuarioAtual = usuarioExistente.get();
            
            // Gerar novo token
            String token = TokenUtil.generateToken();
            LocalDateTime expiryDate = TokenUtil.calculateExpiryDate(emailTokenExpiryHours);
            
            // Atualizar usuário
            usuarioAtual.setEmailVerificationToken(token);
            usuarioAtual.setEmailVerificationExpires(expiryDate);
            usuarioAtual.setEmailVerified(false);
            
            // Salvar usuário
            salvaUsuarioPort.salvar(usuarioAtual);
            
            // Enviar email
            String verificationLink = appUrl + "/verificar-email?token=" + token;
            
            // Usar Thymeleaf para processar o template
            Context context = new Context();
            context.setVariable("nome", usuarioAtual.getNome());
            context.setVariable("verificationLink", verificationLink);
            context.setVariable("validHours", emailTokenExpiryHours);
            
            String emailContent = emailTemplateEngine.process("email/verification-email", context);
            
            brevoServicePort.sendEmail(
                    usuarioAtual.getEmail(),
                    usuarioAtual.getNome(),
                    "Verificação de Email - VintiPay",
                    emailContent
            );
            
            log.info("Email de verificação enviado para: {}", usuarioAtual.getEmail());
            return true;
        } catch (Exception e) {
            log.error("Erro ao enviar email de verificação: ", e);
            return false;
        }
    }
    
    @Override
    public Optional<Usuario> validarTokenVerificacao(String token) {
        if (token == null || token.isEmpty()) {
            log.error("Token inválido");
            return Optional.empty();
        }
        
        try {
            Optional<Usuario> usuarioOpt = buscaUsuarioPort.buscarPorEmailVerificationToken(token);
            
            if (usuarioOpt.isEmpty()) {
                log.error("Token não encontrado");
                return Optional.empty();
            }
            
            Usuario usuario = usuarioOpt.get();
            
            // Verificar se o token expirou
            if (TokenUtil.isTokenExpired(usuario.getEmailVerificationExpires())) {
                log.error("Token expirado");
                return Optional.empty();
            }
            
            // Atualizar usuário
            usuario.setEmailVerified(true);
            usuario.setEmailVerificationToken(null);
            usuario.setEmailVerificationExpires(null);
            
            // Salvar usuário
            salvaUsuarioPort.salvar(usuario);
            
            log.info("Email verificado com sucesso para: {}", usuario.getEmail());
            return Optional.of(usuario);
        } catch (Exception e) {
            log.error("Erro ao validar token: ", e);
            return Optional.empty();
        }
    }
    
    @Override
    public boolean reenviarEmailVerificacao(String email) {
        if (email == null || email.isEmpty()) {
            log.error("Email inválido");
            return false;
        }
        
        try {
            Optional<Usuario> usuarioOpt = buscaUsuarioPort.buscarPorEmail(email);
            
            if (usuarioOpt.isEmpty()) {
                log.error("Usuário não encontrado: {}", email);
                return false;
            }
            
            Usuario usuario = usuarioOpt.get();
            
            // Verificar se o email já foi verificado
            if (usuario.isEmailVerified()) {
                log.info("Email já verificado: {}", email);
                return true;
            }
            
            return criarTokenVerificacaoEEnviarEmail(usuario);
        } catch (Exception e) {
            log.error("Erro ao reenviar email de verificação: ", e);
            return false;
        }
    }
}
