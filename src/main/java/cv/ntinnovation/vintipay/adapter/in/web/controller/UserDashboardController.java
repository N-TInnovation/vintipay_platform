package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.adapter.in.web.security.CustomOAuth2User;
import cv.ntinnovation.vintipay.adapter.out.persistence.entity.UsuarioEntity;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.core.domain.enums.StatusPagamentoEnum;
import cv.ntinnovation.vintipay.application.port.in.BuscaPagamentoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserDashboardController {

    private final BuscaPagamentoUseCase buscaPagamentoUseCase;

    @GetMapping({"", "/"})
    public String dashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        Long usuarioId;
        String nomeUsuario;
        
        if (authentication.getPrincipal() instanceof CustomOAuth2User) {
            CustomOAuth2User customUser = (CustomOAuth2User) authentication.getPrincipal();
            UsuarioEntity usuarioEntity = customUser.getUsuario();
            usuarioId = usuarioEntity.getId();
            nomeUsuario = usuarioEntity.getNome() != null ? usuarioEntity.getNome() : usuarioEntity.getEmail();
            model.addAttribute("usuario", usuarioEntity);
        } else if (authentication.getPrincipal() instanceof Usuario) {
            Usuario usuarioAtual = (Usuario) authentication.getPrincipal();
            usuarioId = usuarioAtual.getId();
            nomeUsuario = usuarioAtual.getNome();
            model.addAttribute("usuario", usuarioAtual);
        } else {
            return "redirect:/login?error";
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
