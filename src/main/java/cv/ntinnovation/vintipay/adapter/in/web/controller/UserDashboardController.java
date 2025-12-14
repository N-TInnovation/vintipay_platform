package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.adapter.in.web.security.CustomOAuth2User;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.core.domain.enums.StatusPagamentoEnum;
import cv.ntinnovation.vintipay.application.port.in.BuscaPagamentoUseCase;
import cv.ntinnovation.vintipay.application.port.out.BuscaUsuario;
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

    @GetMapping({"", "/"})
    public String dashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        Long usuarioId;
        String nomeUsuario;
        
        if (authentication.getPrincipal() instanceof CustomOAuth2User customUser) {
            Usuario usuario = buscaUsuario.buscarPorEmail(customUser.getEmail()).orElse(null);
            usuarioId = usuario.getId();
            nomeUsuario = usuario.getNome() != null ? usuario.getNome() : usuario.getEmail();
            model.addAttribute("usuario", usuario);
            if(usuario.getTenants() == null || usuario.getTenants().isEmpty()) {
                return "redirect:/user/tenant/create";
            }
        }  else {
            log.info("User (else): {}", authentication.getPrincipal());
            throw new RuntimeException("Usuario nao encontrado");
        }
        
        var pageable = PageRequest.of(0, Integer.MAX_VALUE);
        
        var pagamentosPagos = buscaPagamentoUseCase.buscarPorUsuario(pageable, usuarioId, StatusPagamentoEnum.PAGO).getTotalElements();
        var pagamentosPendentes = buscaPagamentoUseCase.buscarPorUsuario(pageable, usuarioId, StatusPagamentoEnum.PENDENTE).getTotalElements();
        var pagamentosCancelados = buscaPagamentoUseCase.buscarPorUsuario(pageable, usuarioId, StatusPagamentoEnum.CANCELADO).getTotalElements();
        var pagamentosEstornados = buscaPagamentoUseCase.buscarPorUsuario(pageable, usuarioId, StatusPagamentoEnum.ESTORNADO).getTotalElements();
        var pagamentosErro = buscaPagamentoUseCase.buscarPorUsuario(pageable, usuarioId, StatusPagamentoEnum.ERRO).getTotalElements();
        var pagamentosExpirados = buscaPagamentoUseCase.buscarPorUsuario(pageable, usuarioId, StatusPagamentoEnum.EXPIRADO).getTotalElements();
        
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
