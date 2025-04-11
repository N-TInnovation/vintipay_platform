package cv.ntinnovation.gateway_payment.adapter.in.web.controller;

import cv.ntinnovation.gateway_payment.application.core.domain.entity.Pagamento;
import cv.ntinnovation.gateway_payment.application.port.in.CriaPagamentoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/pagamento/checkout")
@RequiredArgsConstructor
public class CheckoutPagamentoController {

    private final CriaPagamentoUseCase criaPagamentoUseCase;

    @GetMapping("/{intencaoPagamentoRef}")
    public String checkout(@PathVariable String intencaoPagamentoRef, Model model) {
        // Criar pagamento usando a referência da intenção
        Pagamento pagamento = criaPagamentoUseCase.criar(intencaoPagamentoRef);

        // Adicionar dados ao modelo
        model.addAttribute("pagamento", pagamento);
        
        return "checkout";
    }
}
