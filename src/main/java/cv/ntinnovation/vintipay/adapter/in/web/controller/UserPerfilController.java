package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.adapter.in.web.dto.UserPerfilRequest;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.core.service.CriptografiaService;
import cv.ntinnovation.vintipay.application.port.in.GerenciaUsuarioUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/user/perfil")
@RequiredArgsConstructor
public class UserPerfilController {

    private final CriptografiaService criptografiaService;
    private final GerenciaUsuarioUseCase gerenciaUsuarioUseCase;
    private static final Logger logger = LoggerFactory.getLogger(UserPerfilController.class);

    @GetMapping({"", "/"})
    public String perfilPage(Model model, HttpServletRequest request) {
        // Obter o usuário autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuarioAtual = (Usuario) authentication.getPrincipal();
        
        // Adicionar dados do usuário ao modelo
        model.addAttribute("usuario", usuarioAtual);
        
        // Depuração
        logger.info("Informações do usuário atual: id={}, nome={}, email={}, nif={}", 
                usuarioAtual.getId(), usuarioAtual.getNome(), usuarioAtual.getEmail(), usuarioAtual.getNif());
        
        // Adicionar informações da sessão atual
        String userAgent = request.getHeader("User-Agent");
        String ipAddress = getClientIpAddress(request);
        
        // Detectar o navegador a partir do User-Agent
        String browser = detectBrowser(userAgent);
        
        model.addAttribute("sessaoAtual", Map.of(
            "navegador", browser,
            "ip", ipAddress,
            "dataHora", usuarioAtual.getUltimoLogin() != null ? usuarioAtual.getUltimoLogin() : LocalDateTime.now()
        ));
        
        // Adicionar possíveis mensagens de alerta
        if (!model.containsAttribute("mensagemSucesso")) {
            model.addAttribute("mensagemSucesso", null);
        }
        
        if (!model.containsAttribute("mensagemErro")) {
            model.addAttribute("mensagemErro", null);
        }
        
        return "user/perfil";
    }
    
    /**
     * Detecta o navegador a partir do User-Agent
     */
    private String detectBrowser(String userAgent) {
        if (userAgent == null) return "Desconhecido";
        
        if (userAgent.contains("Firefox")) {
            return "Firefox";
        } else if (userAgent.contains("Chrome") && !userAgent.contains("Edg") && !userAgent.contains("OPR")) {
            return "Chrome";
        } else if (userAgent.contains("Safari") && !userAgent.contains("Chrome")) {
            return "Safari";
        } else if (userAgent.contains("Edg")) {
            return "Edge";
        } else if (userAgent.contains("OPR") || userAgent.contains("Opera")) {
            return "Opera";
        } else {
            return "Outro";
        }
    }
    
    /**
     * Obtém o endereço IP real do cliente, considerando proxies
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        
        // Se estivermos atrás de múltiplos proxies, pegue o primeiro IP
        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0].trim();
        }
        
        return ipAddress;
    }
    
    @PostMapping("/atualizar")
    public String atualizarPerfil(
            @ModelAttribute UserPerfilRequest usuarioRequest,
            RedirectAttributes redirectAttributes) {
        
        // Log detalhado da requisição para diagnóstico
        logger.info("Processando atualização de perfil para usuário com nome: {}", usuarioRequest.getNome());
        
        try {
            // Obter o usuário autenticado
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Usuario usuarioAtual = (Usuario) authentication.getPrincipal();
            
            // Atualizar dados do usuário (simulação)
            usuarioAtual.setNome(usuarioRequest.getNome());
            usuarioAtual.setTelefone(usuarioRequest.getTelefone());
            usuarioAtual.setNif(usuarioRequest.getNif());
            
            // Em um cenário real, aqui chamaríamos um serviço para persistir as alterações
            gerenciaUsuarioUseCase.atualizarUsuario(usuarioAtual);
            
            // Adicionar mensagem de sucesso
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Perfil atualizado com sucesso!");
            logger.info("Perfil atualizado com sucesso para o usuário: {}", usuarioAtual.getNome());
        } catch (Exception e) {
            // Adicionar mensagem de erro
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao atualizar perfil: " + e.getMessage());
            logger.error("Erro ao atualizar perfil: ", e);
        }
        
        return "redirect:/user/perfil";
    }
    
    @PostMapping("/alterar-senha")
    public String alterarSenha(
            @RequestParam("senhaAtual") String senhaAtual,
            @RequestParam("novaSenha") String novaSenha,
            @RequestParam("confirmarSenha") String confirmarSenha,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Obter o usuário autenticado
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Usuario usuarioAtual = (Usuario) authentication.getPrincipal();
            
            // Verificar se as senhas coincidem
            if (!novaSenha.equals(confirmarSenha)) {
                redirectAttributes.addFlashAttribute("mensagemErroSenha", "As senhas não coincidem.");
                return "redirect:/user/perfil";
            }
            
            // Verificar se a senha atual está correta
            if (!criptografiaService.verificar(senhaAtual, usuarioAtual.getPassword())) {
                redirectAttributes.addFlashAttribute("mensagemErroSenha", "A senha atual está incorreta.");
                return "redirect:/user/perfil";
            }
            
            // Em um cenário real, aqui atualizaríamos a nova senha
            String senhaCriptografada = criptografiaService.criptografar(novaSenha);
            gerenciaUsuarioUseCase.alterarSenha(usuarioAtual.getId(), senhaCriptografada);
            
            // Adicionar mensagem de sucesso
            redirectAttributes.addFlashAttribute("mensagemSucessoSenha", "Senha alterada com sucesso!");
        } catch (Exception e) {
            // Adicionar mensagem de erro
            redirectAttributes.addFlashAttribute("mensagemErroSenha", "Erro ao alterar senha: " + e.getMessage());
        }
        
        return "redirect:/user/perfil";
    }
}
