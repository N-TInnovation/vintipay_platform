package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.adapter.out.db.repository.UsuarioRepository;
import cv.ntinnovation.vintipay.adapter.out.db.mapper.UsusarioMapper;
import cv.ntinnovation.vintipay.application.core.domain.entity.Plano;
import cv.ntinnovation.vintipay.application.core.domain.entity.PlanoAssinatura;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.port.in.GerenciaAssinaturaUseCase;
import cv.ntinnovation.vintipay.application.port.in.GerenciaPlanoUseCase;
import cv.ntinnovation.vintipay.application.port.in.StripeIntegrationUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/assinaturas")
@RequiredArgsConstructor
public class AssinaturaController {

    private final GerenciaPlanoUseCase gerenciaPlanoUseCase;
    private final GerenciaAssinaturaUseCase gerenciaAssinaturaUseCase;
    private final StripeIntegrationUseCase stripeIntegrationUseCase;
    private final UsuarioRepository usuarioRepository;
    private final UsusarioMapper usuarioMapper;
    
    @Value("${app.stripe.success-url}")
    private String successUrl;
    
    @Value("${app.stripe.cancel-url}")
    private String cancelUrl;

    @GetMapping
    public String exibirPlanos(Model model) {
        List<Plano> planos = gerenciaPlanoUseCase.listarPlanosAtivos();
        model.addAttribute("planos", planos);

        // Verificar se o usuário já tem uma assinatura
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Usuario usuario = usuarioRepository.findByUsername(username)
                .map(usuarioMapper::toDomain)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Optional<PlanoAssinatura> assinaturaOptional = gerenciaAssinaturaUseCase.buscarAssinaturaPorUsuario(usuario);
        assinaturaOptional.ifPresent(assinatura -> model.addAttribute("assinaturaAtual", assinatura));

        return "assinaturas/planos";
    }

    @GetMapping("/checkout/{planoId}")
    public String checkout(@PathVariable Long planoId, Model model) {
        Optional<Plano> planoOptional = gerenciaPlanoUseCase.buscarPlanoPorId(planoId);
        if (planoOptional.isEmpty()) {
            return "redirect:/assinaturas?erro=Plano+não+encontrado";
        }

        Plano plano = planoOptional.get();
        model.addAttribute("plano", plano);

        return "assinaturas/checkout";
    }

    @PostMapping("/assinar/{planoId}")
    public String assinarPlano(@PathVariable Long planoId) {
        try {
            // Obter o usuário atual
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            Usuario usuario = usuarioRepository.findByUsername(username)
                    .map(usuarioMapper::toDomain)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            // Obter o plano
            Optional<Plano> planoOptional = gerenciaPlanoUseCase.buscarPlanoPorId(planoId);
            if (planoOptional.isEmpty()) {
                return "redirect:/assinaturas?erro=Plano+não+encontrado";
            }

            Plano plano = planoOptional.get();

            // Criar URL de checkout do Stripe usando as variáveis de ambiente
            String checkoutUrl = stripeIntegrationUseCase.criarCheckoutSession(
                    usuario,
                    plano.getStripePriceId(),
                    successUrl,
                    cancelUrl
            );

            // Redirecionar para a página de checkout do Stripe
            return "redirect:" + checkoutUrl;
        } catch (Exception e) {
            log.error("Erro ao criar sessão de checkout", e);
            return "redirect:/assinaturas?erro=" + e.getMessage();
        }
    }

    @GetMapping("/sucesso")
    public String assinaturaComSucesso() {
        return "assinaturas/sucesso";
    }

    @GetMapping("/cancelado")
    public String assinaturaCancelada() {
        return "assinaturas/cancelado";
    }

    @PostMapping("/cancelar")
    public String cancelarAssinatura() {
        try {
            // Obter o usuário atual
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            Usuario usuario = usuarioRepository.findByUsername(username)
                    .map(usuarioMapper::toDomain)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            // Cancelar a assinatura
            gerenciaAssinaturaUseCase.cancelarAssinatura(usuario);

            return "redirect:/assinaturas?sucesso=Assinatura+cancelada+com+sucesso";
        } catch (Exception e) {
            log.error("Erro ao cancelar assinatura", e);
            return "redirect:/assinaturas?erro=" + e.getMessage();
        }
    }
}