package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user/assinatura")
@RequiredArgsConstructor
public class UserAssinaturaController {

    @GetMapping({"", "/"})
    public String assinaturaPage(Model model) {
        // Obter o usuário autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuarioAtual = (Usuario) authentication.getPrincipal();
        
        // Adicionar dados do usuário ao modelo
        model.addAttribute("usuario", usuarioAtual);
        
        // Simulação de dados de assinatura (em um cenário real, esses dados viriam de um serviço)
        model.addAttribute("planoAtual", "Plano Básico");
        model.addAttribute("dataInicio", "01/01/2025");
        model.addAttribute("dataRenovacao", "01/01/2026");
        model.addAttribute("statusAssinatura", "Ativa");
        model.addAttribute("valorMensal", "R$ 29,90");
        
        // Simulação de recursos do plano
        String[] recursos = {
            "Processamento de até 100 pagamentos por mês",
            "Suporte por e-mail",
            "Relatórios básicos",
            "API de integração"
        };
        model.addAttribute("recursos", recursos);
        
        // Simulação de planos disponíveis para upgrade
        model.addAttribute("planosDisponiveis", new String[]{"Plano Profissional", "Plano Enterprise"});
        
        return "user/assinatura";
    }
}
