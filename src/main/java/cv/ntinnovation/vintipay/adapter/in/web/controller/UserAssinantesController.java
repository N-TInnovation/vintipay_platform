package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.adapter.in.web.security.CustomOAuth2User;
import cv.ntinnovation.vintipay.application.core.domain.enums.AmbienteEnum;
import cv.ntinnovation.vintipay.application.core.domain.enums.StatusSubscricaoPagamentoEnum;
import cv.ntinnovation.vintipay.application.core.service.AmbienteService;
import cv.ntinnovation.vintipay.application.port.in.BuscaSubscricaoPagamentoUseCase;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequestMapping("/user/assinantes")
@RequiredArgsConstructor
public class UserAssinantesController {

    private final BuscaSubscricaoPagamentoUseCase buscaSubscricaoPagamentoUseCase;
    private final AmbienteService ambienteService;
    private static final int PAGE_SIZE = 10;

    @GetMapping({"", "/"})
    public String listarAssinantes(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "status", required = false) StatusSubscricaoPagamentoEnum status,
            @RequestParam(name = "ambiente", required = false) AmbienteEnum ambiente,
            HttpServletRequest request,
            Model model,
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        
        log.info("Acessando página de assinantes");
        
        AmbienteEnum ambienteResolvido = ambienteService.resolveAmbiente(request, ambiente);
        
        Long tenantId = (Long) request.getSession().getAttribute("tenantId");
        
        if (tenantId == null) {
            log.warn("Tenant não encontrado na sessão");
            model.addAttribute("erro", "Selecione uma organização para visualizar assinantes");
            return "user/assinantes";
        }
        
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("created_at").descending());
        
        try {
            var assinantes = status != null 
                ? buscaSubscricaoPagamentoUseCase.buscarPorTenantEAmbienteEStatus(pageable, tenantId, status, ambienteResolvido)
                : buscaSubscricaoPagamentoUseCase.buscarPorTenantEAmbiente(pageable, tenantId, ambienteResolvido);
            
            model.addAttribute("assinantes", assinantes);
            model.addAttribute("statusAtual", status);
            model.addAttribute("ambienteAtual", ambienteResolvido);
            model.addAttribute("statusOptions", StatusSubscricaoPagamentoEnum.values());
            model.addAttribute("ambienteOptions", AmbienteEnum.values());
            model.addAttribute("paginaAtual", page);
            
            var assinantesAtivas = buscaSubscricaoPagamentoUseCase.buscarPorTenantEAmbienteEStatus(
                    PageRequest.of(0, Integer.MAX_VALUE), 
                    tenantId, 
                    StatusSubscricaoPagamentoEnum.INSCRICAO_ATIVA,
                    ambienteResolvido
            ).getTotalElements();
            
            var assinantesCanceladas = buscaSubscricaoPagamentoUseCase.buscarPorTenantEAmbienteEStatus(
                    PageRequest.of(0, Integer.MAX_VALUE), 
                    tenantId, 
                    StatusSubscricaoPagamentoEnum.INSCRICAO_CANCELADA,
                    ambienteResolvido
            ).getTotalElements();
            
            var assinantesPendentes = buscaSubscricaoPagamentoUseCase.buscarPorTenantEAmbienteEStatus(
                    PageRequest.of(0, Integer.MAX_VALUE), 
                    tenantId, 
                    StatusSubscricaoPagamentoEnum.INSCRICAO_PENDENTE,
                    ambienteResolvido
            ).getTotalElements();
            
            model.addAttribute("totalAtivas", assinantesAtivas);
            model.addAttribute("totalCanceladas", assinantesCanceladas);
            model.addAttribute("totalPendentes", assinantesPendentes);
            
            log.info("Total de assinantes encontrados: {}", assinantes.getTotalElements());
            
            return "user/assinantes";
            
        } catch (Exception e) {
            log.error("Erro ao buscar assinantes: {}", e.getMessage(), e);
            model.addAttribute("erro", "Erro ao buscar assinantes: " + e.getMessage());
            return "user/assinantes";
        }
    }
}
