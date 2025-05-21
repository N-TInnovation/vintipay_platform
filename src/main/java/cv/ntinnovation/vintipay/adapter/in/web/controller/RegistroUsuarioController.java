package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.adapter.in.web.dto.RegistroUsuarioRequest;
import cv.ntinnovation.vintipay.application.core.domain.entity.Plano;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.port.in.GerenciaPlanoUseCase;
import cv.ntinnovation.vintipay.application.port.in.RegistroUsuarioUseCase;
import cv.ntinnovation.vintipay.application.port.in.StripeIntegrationUseCase;
import cv.ntinnovation.vintipay.application.port.in.VerificaEmailUseCase;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/registro")
@RequiredArgsConstructor
public class RegistroUsuarioController {

    private final RegistroUsuarioUseCase registroUsuarioUseCase;
    private final GerenciaPlanoUseCase gerenciaPlanoUseCase;
    private final VerificaEmailUseCase verificaEmailUseCase;

    @GetMapping
    public String exibirFormularioRegistro(Model model) {
        // Criar uma instância da solicitação com valor padrão de planoId
        RegistroUsuarioRequest registroRequest = new RegistroUsuarioRequest();
        registroRequest.setPlanoId(1L); // Definir plano padrão
        
        // Adicionar o objeto de requisição ao modelo
        model.addAttribute("registroRequest", registroRequest);
        
        // Buscar todos os planos disponíveis para referência
        List<Plano> planosDisponiveis = gerenciaPlanoUseCase.listarTodosPlanos();
        model.addAttribute("planosDisponiveis", planosDisponiveis);
        
        // Usar o novo template com formato de wizard
        return "registro-wizard";
    }

    @PostMapping
    public String registrarUsuario(@Valid @ModelAttribute("registroRequest") RegistroUsuarioRequest request,
                                  BindingResult result, Model model, HttpServletRequest servletRequest,
                                  RedirectAttributes redirectAttributes) {

        
        log.info("Tentando registrar usuário: {}", request.getUsername());
        
        if (result.hasErrors()) {
            log.warn("Erros de validação: {}", result.getAllErrors());

            // Usar o mesmo template que foi usado para exibir o formulário
            return "registro-wizard";
        }

        try {
            // Registrar o usuário com o plano básico diretamente
            Usuario usuario = registroUsuarioUseCase.registrarNovoUsuarioSemPlano(
                    request.getUsername(),
                    request.getEmail(),
                    request.getNome(),
                    request.getTelefone(),
                    request.getNif(),
                    request.getPassword(),
                    "USER"
            );
            
            log.info("Usuário registrado com sucesso: {}", usuario.getUsername());

            // Enviar email de verificação
            boolean emailEnviado = verificaEmailUseCase.criarTokenVerificacaoEEnviarEmail(usuario);

            if (emailEnviado) {
                log.info("Email de verificação enviado para: {}", usuario.getEmail());
                redirectAttributes.addFlashAttribute("mensagem",
                    "Registro concluído com sucesso! Um email de verificação foi enviado para " +
                    usuario.getEmail() + ". Por favor, verifique sua caixa de entrada para ativar sua conta.");
            } else {
                log.warn("Falha ao enviar email de verificação para: {}", usuario.getEmail());
                redirectAttributes.addFlashAttribute("mensagem",
                    "Registro concluído com sucesso! Sua conta foi criada, mas não foi possível enviar o email de verificação. " +
                    "Entre em contato com o suporte.");
            }

            
            return "redirect:/login?registroSucesso";
            
        } catch (Exception e) {
            log.error("Erro ao registrar usuário: {}", e.getMessage(), e);
            
            // Verificar se o erro é relacionado a um usuário duplicado
            String errorMessage = e.getMessage();
            if (errorMessage != null && errorMessage.contains("username")) {
                // Erro específico para username duplicado
                model.addAttribute("erroUsername", "Este nome de usuário já está em uso. Por favor, escolha outro.");
            } else if (errorMessage != null && errorMessage.contains("email")) {
                // Erro específico para email duplicado
                model.addAttribute("erroEmail", "Este email já está cadastrado no sistema.");
            } else {
                // Erro genérico
                model.addAttribute("erro", "Erro ao registrar usuário: " + e.getMessage());
            }
            
            // Preservar os dados do formulário
            model.addAttribute("registroRequest", request);
            
            // Usar o mesmo template que foi usado para exibir o formulário
            return "registro-wizard";
        }
    }

}
