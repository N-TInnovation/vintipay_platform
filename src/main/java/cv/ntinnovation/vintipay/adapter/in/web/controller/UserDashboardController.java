package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.adapter.in.web.security.CustomOAuth2User;
import cv.ntinnovation.vintipay.application.core.domain.entity.Tenant;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.core.domain.enums.StatusPagamentoEnum;
import cv.ntinnovation.vintipay.application.core.service.TenentService;
import cv.ntinnovation.vintipay.application.port.in.BuscaPagamentoUseCase;
import cv.ntinnovation.vintipay.application.port.out.BuscaUsuario;
import cv.ntinnovation.vintipay.application.port.out.GerenciaTenantPort;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserDashboardController {

    private final BuscaPagamentoUseCase buscaPagamentoUseCase;
    private final BuscaUsuario buscaUsuario;
    private final TenentService tenentService;
    private final GerenciaTenantPort gerenciaTenantPort;

    @GetMapping({"", "/"})
    public String dashboard(Model model, HttpServletRequest request, HttpSession session) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        Long usuarioId;
        String nomeUsuario;
        
        if (authentication.getPrincipal() instanceof CustomOAuth2User customUser) {
            Usuario usuario = buscaUsuario.buscarPorEmail(customUser.getEmail()).orElse(null);
            usuarioId = usuario.getId();
            nomeUsuario = usuario.getNome() != null ? usuario.getNome() : usuario.getEmail();
            model.addAttribute("usuario", usuario);
        }  else {
            log.info("User (else): {}", authentication.getPrincipal());
            throw new RuntimeException("Usuario nao encontrado");
        }

        Long tenantId = (Long) session.getAttribute("tenantId");

        var pageable = PageRequest.of(0, Integer.MAX_VALUE);
        
        var pagamentosPagos = buscaPagamentoUseCase.buscarPorUsuarioETenant(pageable, usuarioId, tenantId, StatusPagamentoEnum.PAGO).getTotalElements();
        var pagamentosPendentes = buscaPagamentoUseCase.buscarPorUsuarioETenant(pageable, usuarioId, tenantId, StatusPagamentoEnum.PENDENTE).getTotalElements();
        var pagamentosCancelados = buscaPagamentoUseCase.buscarPorUsuarioETenant(pageable, usuarioId, tenantId, StatusPagamentoEnum.CANCELADO).getTotalElements();
        var pagamentosEstornados = buscaPagamentoUseCase.buscarPorUsuarioETenant(pageable, usuarioId, tenantId, StatusPagamentoEnum.ESTORNADO).getTotalElements();
        var pagamentosErro = buscaPagamentoUseCase.buscarPorUsuarioETenant(pageable, usuarioId, tenantId, StatusPagamentoEnum.ERRO).getTotalElements();
        var pagamentosExpirados = buscaPagamentoUseCase.buscarPorUsuarioETenant(pageable, usuarioId, tenantId, StatusPagamentoEnum.EXPIRADO).getTotalElements();
        
        var totalPagamentos = pagamentosPagos + pagamentosPendentes + pagamentosCancelados + pagamentosEstornados + pagamentosErro + pagamentosExpirados;

        model.addAttribute("totalPagamentos", totalPagamentos);
        model.addAttribute("pagamentosPagos", pagamentosPagos);
        model.addAttribute("pagamentosPendentes", pagamentosPendentes);
        model.addAttribute("pagamentosCancelados", pagamentosCancelados);
        model.addAttribute("pagamentosEstornados", pagamentosEstornados);
        model.addAttribute("pagamentosErro", pagamentosErro);
        model.addAttribute("pagamentosExpirados", pagamentosExpirados);

        return "user/dashboard";
    }
}
