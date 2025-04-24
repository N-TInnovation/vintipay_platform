package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.adapter.in.web.dto.RegistroUsuarioRequest;
import cv.ntinnovation.vintipay.application.core.domain.entity.Plano;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.port.in.GerenciaPlanoUseCase;
import cv.ntinnovation.vintipay.application.port.in.RegistroUsuarioUseCase;
import cv.ntinnovation.vintipay.application.port.in.StripeIntegrationUseCase;
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
import org.springframework.web.bind.annotation.RequestParam;
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
    private final StripeIntegrationUseCase stripeIntegrationUseCase;

    @GetMapping
    public String exibirFormularioRegistro(Model model) {
        // Adicionar o objeto de requisição ao modelo
        model.addAttribute("registroRequest", new RegistroUsuarioRequest());
        
        // Buscar todos os planos disponíveis
        List<Plano> planosDisponiveis = gerenciaPlanoUseCase.listarTodosPlanos();
        model.addAttribute("planosDisponiveis", planosDisponiveis);
        
        // Usar o novo template com formato de wizard
        return "registro-wizard";
    }

    @PostMapping
    public String registrarUsuario(@Valid @ModelAttribute("registroRequest") RegistroUsuarioRequest request,
                                  BindingResult result, Model model, HttpServletRequest servletRequest,
                                  RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            // Recarregar os planos disponíveis em caso de erro
            List<Plano> planosDisponiveis = gerenciaPlanoUseCase.listarTodosPlanos();
            model.addAttribute("planosDisponiveis", planosDisponiveis);
            // Usar o novo template com formato de wizard
            return "registro-wizard";
        }

        try {
            // Registrar o usuário sem criar assinatura ainda
            Usuario usuario = registroUsuarioUseCase.registrarNovoUsuarioSemPlano(
                    request.getUsername(),
                    request.getEmail(),
                    request.getNome(),
                    request.getTelefone(),
                    request.getPassword(),
                    "USER"
            );
            
            // Obter o plano selecionado
            Optional<Plano> planoOptional = gerenciaPlanoUseCase.buscarPlanoPorId(request.getPlanoId());
            if (planoOptional.isEmpty()) {
                throw new RuntimeException("Plano não encontrado");
            }
            
            Plano plano = planoOptional.get();
            
            // Construir URLs de sucesso e cancelamento
            String baseUrl = servletRequest.getScheme() + "://" + servletRequest.getServerName();
            if (servletRequest.getServerPort() != 80 && servletRequest.getServerPort() != 443) {
                baseUrl += ":" + servletRequest.getServerPort();
            }
            
            String successUrl = baseUrl + "/registro/checkout-success?username=" + usuario.getUsername() + "&planoId=" + plano.getId();
            String cancelUrl = baseUrl + "/registro/checkout-cancel?username=" + usuario.getUsername();
            
            // Criar sessão de checkout do Stripe
            String checkoutUrl = stripeIntegrationUseCase.criarCheckoutSession(
                    usuario,
                    plano.getStripePriceId(),
                    successUrl,
                    cancelUrl
            );
            
            // Redirecionar para o checkout do Stripe
            return "redirect:" + checkoutUrl;
            
        } catch (Exception e) {
            log.error("Erro ao registrar usuário", e);
            model.addAttribute("erro", "Erro ao registrar usuário: " + e.getMessage());
            
            // Recarregar os planos disponíveis em caso de erro
            List<Plano> planosDisponiveis = gerenciaPlanoUseCase.listarTodosPlanos();
            model.addAttribute("planosDisponiveis", planosDisponiveis);
            
            // Usar o novo template com formato de wizard
            return "registro-wizard";
        }
    }
    
    @GetMapping("/checkout-success")
    public String checkoutSuccess(@RequestParam String username, @RequestParam Long planoId, 
                                 RedirectAttributes redirectAttributes) {
        try {
            // Criar assinatura para o usuário após pagamento bem-sucedido
            boolean success = registroUsuarioUseCase.criarAssinaturaAposRegistro(username, planoId);
            
            if (success) {
                redirectAttributes.addFlashAttribute("mensagem", 
                    "Registro concluído com sucesso! Sua assinatura está ativa.");
            } else {
                redirectAttributes.addFlashAttribute("alerta", 
                    "Registro concluído, mas houve um problema ao ativar sua assinatura. Entre em contato com o suporte.");
            }
            
            return "redirect:/login?registroSucesso";
        } catch (Exception e) {
            log.error("Erro ao finalizar registro após checkout", e);
            redirectAttributes.addFlashAttribute("erro", 
                "Ocorreu um erro ao finalizar seu registro: " + e.getMessage());
            return "redirect:/login?erro";
        }
    }
    
    @GetMapping("/checkout-cancel")
    public String checkoutCancel(@RequestParam String username, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("alerta", 
            "O pagamento foi cancelado, mas seu cadastro foi realizado. Você pode fazer login e assinar um plano depois.");
        return "redirect:/login";
    }
}
