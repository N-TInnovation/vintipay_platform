package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.port.in.VerificaEmailUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping
@RequiredArgsConstructor
public class VerificacaoEmailController {
    
    private final VerificaEmailUseCase verificaEmailUseCase;
    
    /**
     * Endpoint para verificar o email usando um token
     */
    @GetMapping("/verificar-email")
    public String verificarEmail(@RequestParam(value = "token", required = false) String token, Model model) {
        if (token == null || token.isEmpty()) {
            model.addAttribute("mensagem", "Token inválido.");
            model.addAttribute("tipo", "erro");
            return "verificacao-email";
        }
        
        Optional<Usuario> usuarioOpt = verificaEmailUseCase.validarTokenVerificacao(token);
        
        if (usuarioOpt.isPresent()) {
            model.addAttribute("mensagem", "Email verificado com sucesso! Agora você pode fazer login.");
            model.addAttribute("tipo", "sucesso");
        } else {
            model.addAttribute("mensagem", "Token inválido ou expirado. Por favor, solicite um novo link de verificação.");
            model.addAttribute("tipo", "erro");
            model.addAttribute("reenvio", true);
        }
        
        return "verificacao-email";
    }
    
    /**
     * Página para solicitar reenvio do email de verificação
     */
    @GetMapping("/reenviar-verificacao")
    public String paginaReenvioVerificacao(Model model) {
        model.addAttribute("reenvio", true);
        return "verificacao-email";
    }
    
    /**
     * Endpoint para reenviar o email de verificação
     */
    @PostMapping("/reenviar-verificacao")
    public String reenviarVerificacao( @RequestParam("email") String email, Model model) {

        log.info("Reenviando email de verificação para: {}", email);

        if (email == null || email.isEmpty()) {
            model.addAttribute("mensagem", "Email inválido.");
            model.addAttribute("tipo", "erro");
            return "verificacao-email";
        }

        boolean enviado = verificaEmailUseCase.reenviarEmailVerificacao(email);
        
        if (enviado) {
            model.addAttribute("mensagem", "Um novo email de verificação foi enviado. Por favor, verifique sua caixa de entrada.");
            model.addAttribute("tipo", "sucesso");
        } else {
            model.addAttribute("mensagem", "Não foi possível enviar o email de verificação. Verifique se o email está correto.");
            model.addAttribute("tipo", "erro");
            model.addAttribute("reenvio", true);
        }
        
        return "verificacao-email";
    }
}
