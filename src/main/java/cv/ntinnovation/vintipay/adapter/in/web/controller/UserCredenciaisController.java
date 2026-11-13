package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.adapter.in.web.security.CustomOAuth2User;
import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoPagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.core.domain.rules.FingerPrintService;
import cv.ntinnovation.vintipay.application.port.in.GerenciaCredenciaisConfiguracaoUseCase;
import cv.ntinnovation.vintipay.application.port.out.BuscaConfiguracaoPagamentoPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequestMapping("/user/credenciais")
@RequiredArgsConstructor
public class UserCredenciaisController {

    private final BuscaConfiguracaoPagamentoPort buscaConfiguracaoPagamentoPort;
    private final GerenciaCredenciaisConfiguracaoUseCase gerenciaCredenciaisConfiguracaoUseCase;
    private final FingerPrintService fingerPrintService;

    @GetMapping("/{id}")
    public String exibirCredenciais(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes, @AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        Usuario usuarioAtual = customOAuth2User.getUsuario();

        var configuracaoOpt = buscaConfiguracaoPagamentoPort.buscarPorId(id);

        if (configuracaoOpt.isPresent()) {
            ConfiguracaoPagamento configuracao = configuracaoOpt.get();

            if (configuracao.getUsuarioId() != null && configuracao.getUsuarioId().equals(usuarioAtual.getId())) {
                model.addAttribute("configuracao", prepararConfiguracaoParaExibicao(configuracao));
                return "user/credenciais";
            }
        }

        redirectAttributes.addFlashAttribute("mensagemErro", "Configuração não encontrada ou você não tem permissão para acessá-la.");
        return "redirect:/user/config-pagamentos";
    }

    @GetMapping("/renovar-api-secret/{id}")
    public String renovarApiSecretGet(@PathVariable Long id, RedirectAttributes redirectAttributes, @AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        return renovarApiSecret(id, redirectAttributes, customOAuth2User);
    }

    @PostMapping("/renovar-api-secret/{id}")
    public String renovarApiSecretPost(@PathVariable Long id, RedirectAttributes redirectAttributes, @AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        return renovarApiSecret(id, redirectAttributes, customOAuth2User);
    }

    private String renovarApiSecret(Long id, RedirectAttributes redirectAttributes, CustomOAuth2User customOAuth2User) {
        try {
            String novoApiSecret = gerenciaCredenciaisConfiguracaoUseCase.renovarApiSecret(id, customOAuth2User.getUsuario().getId());
            redirectAttributes.addFlashAttribute("novoApiSecret", novoApiSecret);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "API Secret renovado com sucesso! Guarde-o em um local seguro, pois ele não será exibido novamente.");
            return "redirect:/user/credenciais/" + id;
        } catch (Exception e) {
            log.error("Erro ao renovar API Secret: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao renovar API Secret: " + e.getMessage());
            return "redirect:/user/config-pagamentos";
        }
    }

    @PostMapping("/renovar-return-aut-code/{id}")
    public String renovarReturnAutCode(@PathVariable Long id, RedirectAttributes redirectAttributes, @AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        try {
            String novoReturnAutCode = gerenciaCredenciaisConfiguracaoUseCase.renovarReturnAutCode(id, customOAuth2User.getUsuario().getId());
            redirectAttributes.addFlashAttribute("novoReturnAutCode", novoReturnAutCode);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Return Aut Code renovado com sucesso! Guarde-o em um local seguro, pois ele não será exibido novamente.");
            return "redirect:/user/credenciais/" + id;
        } catch (Exception e) {
            log.error("Erro ao renovar Return Aut Code: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao renovar Return Aut Code: " + e.getMessage());
            return "redirect:/user/config-pagamentos";
        }
    }

    @PostMapping("/renovar-webhook-secret/{id}")
    public String renovarWebhookSecret(@PathVariable Long id, RedirectAttributes redirectAttributes, @AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        try {
            String novoWebhookSecret = gerenciaCredenciaisConfiguracaoUseCase.renovarWebhookSecret(id, customOAuth2User.getUsuario().getId());
            redirectAttributes.addFlashAttribute("novoWebhookSecret", novoWebhookSecret);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Webhook Secret renovado com sucesso! Guarde-o em um local seguro, pois ele não será exibido novamente.");
            return "redirect:/user/credenciais/" + id;
        } catch (Exception e) {
            log.error("Erro ao renovar Webhook Secret: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao renovar Webhook Secret: " + e.getMessage());
            return "redirect:/user/config-pagamentos";
        }
    }

    private ConfiguracaoPagamento prepararConfiguracaoParaExibicao(ConfiguracaoPagamento configuracao) {
        if (configuracao.getPosAutCode() == null || configuracao.getPosAutCode().isEmpty()) {
            return configuracao;
        }

        try {
            String posAutCodeDescriptografado = fingerPrintService.decryptAES256(configuracao.getPosAutCode());
            configuracao.setPosAutCode(posAutCodeDescriptografado);
            return configuracao;
        } catch (Exception e) {
            log.warn("Erro ao descriptografar POS Aut Code: {}", e.getMessage(), e);
            return configuracao;
        }
    }
}
