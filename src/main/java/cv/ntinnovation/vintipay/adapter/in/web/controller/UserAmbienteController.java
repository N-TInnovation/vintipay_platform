package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.adapter.in.web.security.CustomOAuth2User;
import cv.ntinnovation.vintipay.application.core.domain.enums.AmbienteEnum;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user/ambiente")
@RequiredArgsConstructor
@Slf4j
public class UserAmbienteController {

    @GetMapping("/switch/{ambiente}")
    public String switchAmbiente(@PathVariable String ambiente,
                                HttpServletRequest httpServletRequest,
                                RedirectAttributes redirectAttributes) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication.getPrincipal() instanceof CustomOAuth2User customUser) {
                
                // Validar se o ambiente é válido
                AmbienteEnum ambienteEnum;
                try {
                    ambienteEnum = AmbienteEnum.valueOf(ambiente.toUpperCase());
                } catch (IllegalArgumentException e) {
                    log.warn("Ambiente inválido informado: {}", ambiente);
                    redirectAttributes.addFlashAttribute("erro", "Ambiente inválido");
                    return "redirect:/user";
                }
                
                // Atualizar sessão com o novo ambiente
                httpServletRequest.getSession().setAttribute("ambiente", ambienteEnum);
                
                log.info("Usuário {} trocou para ambiente {}", customUser.getEmail(), ambienteEnum.getAmbiente());
                redirectAttributes.addFlashAttribute("sucesso", "Ambiente alterado para: " + ambienteEnum.getAmbiente());
                
            } else {
                throw new RuntimeException("Usuário não autenticado");
            }
            
            return "redirect:/user";
            
        } catch (Exception e) {
            log.error("Erro ao trocar ambiente: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("erro", "Erro ao trocar ambiente: " + e.getMessage());
            return "redirect:/user";
        }
    }
}
