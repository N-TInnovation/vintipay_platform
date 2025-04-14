package cv.ntinnovation.gateway_payment.adapter.in.web.controller;

import cv.ntinnovation.gateway_payment.application.core.domain.enums.StatusPagamentoEnum;
import cv.ntinnovation.gateway_payment.application.port.in.BuscaPagamentoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final BuscaPagamentoUseCase buscaPagamentoUseCase;

    @GetMapping({"", "/"})
    public String dashboard(Model model) {
        var pageable = PageRequest.of(0, Integer.MAX_VALUE);
        
        // Busca totais por status
        var pagamentosPagos = buscaPagamentoUseCase.buscarTodos(pageable, null, StatusPagamentoEnum.PAGO).getTotalElements();
        var pagamentosPendentes = buscaPagamentoUseCase.buscarTodos(pageable, null, StatusPagamentoEnum.PENDENTE).getTotalElements();
        var pagamentosCancelados = buscaPagamentoUseCase.buscarTodos(pageable, null, StatusPagamentoEnum.CANCELADO).getTotalElements();
        var totalPagamentos = pagamentosPagos + pagamentosPendentes + pagamentosCancelados;

        // Adiciona estatísticas ao modelo
        model.addAttribute("totalPagamentos", totalPagamentos);
        model.addAttribute("pagamentosPagos", pagamentosPagos);
        model.addAttribute("pagamentosPendentes", pagamentosPendentes);
        model.addAttribute("pagamentosCancelados", pagamentosCancelados);

        return "admin/dashboard";
    }
}
