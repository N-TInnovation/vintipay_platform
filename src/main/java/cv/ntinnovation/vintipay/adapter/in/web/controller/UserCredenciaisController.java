package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoPagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.port.out.BuscaConfiguracaoPagamentoPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user/credenciais")
@RequiredArgsConstructor
public class UserCredenciaisController {

    private final BuscaConfiguracaoPagamentoPort buscaConfiguracaoPagamentoPort;

    @GetMapping("/{id}")
    public String exibirCredenciais(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        // Obter o usuário autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuarioAtual = (Usuario) authentication.getPrincipal();
        
        // Buscar configuração
        var configuracaoOpt = buscaConfiguracaoPagamentoPort.buscarPorId(id);
        
        if (configuracaoOpt.isPresent()) {
            ConfiguracaoPagamento configuracao = configuracaoOpt.get();
            
            // Verificar se a configuração pertence ao usuário
            if (configuracao.getUsuarioId() != null && configuracao.getUsuarioId().equals(usuarioAtual.getId())) {
                model.addAttribute("configuracao", configuracao);
                return "user/credenciais";
            }
        }
        
        // Redirecionar se não encontrar ou não pertencer ao usuário
        redirectAttributes.addFlashAttribute("mensagemErro", "Configuração não encontrada ou você não tem permissão para acessá-la.");
        return "redirect:/user/config-pagamentos";
    }
}
