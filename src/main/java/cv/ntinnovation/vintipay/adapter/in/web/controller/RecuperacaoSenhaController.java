package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.port.in.BuscaUsuarioUseCase;
import cv.ntinnovation.vintipay.application.port.in.NotificacaoUseCase;
import cv.ntinnovation.vintipay.application.port.in.AtualizaUsuarioUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class RecuperacaoSenhaController {

    private final BuscaUsuarioUseCase buscaUsuarioUseCase;
    private final NotificacaoUseCase notificacaoUseCase;
    private final AtualizaUsuarioUseCase atualizaUsuarioUseCase;
    private final PasswordEncoder passwordEncoder;
    private final SpringTemplateEngine templateEngine;
    
    @Value("${app.url:https://vintipay.cv}")
    private String appUrl;

    @GetMapping("/recuperar-senha")
    public String solicitarRecuperacaoSenha() {
        return "auth/recuperar-senha";
    }

    @PostMapping("/recuperar-senha")
    public String processarSolicitacaoRecuperacao(@RequestParam("email") String email, 
                                                 RedirectAttributes redirectAttributes) {
        log.info("Solicitação de recuperação de senha para o e-mail: {}", email);
        
        // Verificar se o e-mail existe na base de dados
        Optional<Usuario> usuarioOpt = buscaUsuarioUseCase.buscarPorEmail(email);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            
            // Gerar token único para recuperação
            String token = UUID.randomUUID().toString();
            
            // Salvar token e data de expiração (24 horas)
            usuario.setResetPasswordToken(token);
            usuario.setResetPasswordExpires(LocalDateTime.now().plusHours(24));
            atualizaUsuarioUseCase.atualizar(usuario);
            
            // Preparar e enviar e-mail com o link de recuperação
            String resetUrl = appUrl + "/redefinir-senha?token=" + token;
            
            // Preparar o template Thymeleaf para o e-mail
            Context context = new Context();
            context.setVariable("resetLink", resetUrl);
            String emailContent = templateEngine.process("email/recuperacao-senha", context);
            
            // Enviar e-mail usando o serviço de notificação existente
            String assunto = "VintiPay - Recuperação de Senha";
            notificacaoUseCase.enviarEmail(email, usuario.getNome(), assunto, emailContent);
        }
        
        // Por segurança, sempre retornar a mesma resposta independentemente de o e-mail existir ou não
        redirectAttributes.addFlashAttribute("mensagem", 
            "Se o e-mail informado estiver cadastrado em nosso sistema, enviaremos instruções para recuperação de senha.");
        return "redirect:/recuperar-senha/confirmacao";
    }
    
    @GetMapping("/recuperar-senha/confirmacao")
    public String exibirConfirmacao(Model model) {
        return "auth/recuperar-senha-confirmacao";
    }
    
    @GetMapping("/redefinir-senha")
    public String exibirFormularioRedefinicaoSenha(@RequestParam("token") String token, Model model) {
        // Verificar se o token existe e é válido
        Optional<Usuario> usuarioOpt = buscaUsuarioUseCase.buscarPorResetToken(token);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            
            // Verificar se o token ainda é válido (não expirou)
            if (usuario.getResetPasswordExpires().isAfter(LocalDateTime.now())) {
                model.addAttribute("token", token);
                return "auth/redefinir-senha";
            }
        }
        
        // Token inválido ou expirado
        model.addAttribute("error", "O link de recuperação é inválido ou expirou. Por favor, solicite um novo link.");
        return "auth/recuperar-senha";
    }
    
    @PostMapping("/redefinir-senha")
    public String processarRedefinicaoSenha(@RequestParam("token") String token,
                                          @RequestParam("password") String password,
                                          @RequestParam("confirmPassword") String confirmPassword,
                                          Model model,
                                          RedirectAttributes redirectAttributes) {
        
        // Verificar se as senhas coincidem
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "As senhas não coincidem");
            model.addAttribute("token", token);
            return "auth/redefinir-senha";
        }
        
        // Verificar se o token existe e é válido
        Optional<Usuario> usuarioOpt = buscaUsuarioUseCase.buscarPorResetToken(token);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            
            // Verificar se o token ainda é válido (não expirou)
            if (usuario.getResetPasswordExpires().isAfter(LocalDateTime.now())) {
                // Atualizar a senha do usuário
                usuario.setPassword(passwordEncoder.encode(password));
                
                // Limpar o token e a data de expiração
                usuario.setResetPasswordToken(null);
                usuario.setResetPasswordExpires(null);
                
                // Salvar as alterações
                atualizaUsuarioUseCase.atualizar(usuario);
                
                // Redirecionar para a página de login com mensagem de sucesso
                redirectAttributes.addFlashAttribute("successMessage", "Sua senha foi alterada com sucesso. Você pode fazer login agora.");
                return "redirect:/admin/login";
            }
        }
        
        // Token inválido ou expirado
        model.addAttribute("error", "O link de recuperação é inválido ou expirou. Por favor, solicite um novo link.");
        return "auth/recuperar-senha";
    }
}
