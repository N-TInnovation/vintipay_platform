package cv.ntinnovation.vintipay.adapter.in.web.controller;

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
        // Obter o usuário autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuarioAtual = (Usuario) authentication.getPrincipal();
        
        var pageable = PageRequest.of(0, Integer.MAX_VALUE);
        
        // Busca totais por status apenas para o usuário atual
        var pagamentosPagos = buscaPagamentoUseCase.buscarPorUsuario(pageable, usuarioAtual.getId(), StatusPagamentoEnum.PAGO).getTotalElements();
        var pagamentosPendentes = buscaPagamentoUseCase.buscarPorUsuario(pageable, usuarioAtual.getId(), StatusPagamentoEnum.PENDENTE).getTotalElements();
        var pagamentosCancelados = buscaPagamentoUseCase.buscarPorUsuario(pageable, usuarioAtual.getId(), StatusPagamentoEnum.CANCELADO).getTotalElements();
        var totalPagamentos = pagamentosPagos + pagamentosPendentes + pagamentosCancelados;

        // Adiciona estatísticas ao modelo
        model.addAttribute("totalPagamentos", totalPagamentos);
        model.addAttribute("pagamentosPagos", pagamentosPagos);
        model.addAttribute("pagamentosPendentes", pagamentosPendentes);
        model.addAttribute("pagamentosCancelados", pagamentosCancelados);
        model.addAttribute("usuario", usuarioAtual);

        return "user/dashboard";
    }
}
