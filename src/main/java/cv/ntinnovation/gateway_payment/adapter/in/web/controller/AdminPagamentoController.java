package cv.ntinnovation.gateway_payment.adapter.in.web.controller;

import cv.ntinnovation.gateway_payment.adapter.in.web.mapper.PagamentoWebMapper;
import cv.ntinnovation.gateway_payment.application.port.in.BuscaPagamentoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/pagamentos")
@RequiredArgsConstructor
public class AdminPagamentoController {

    private final BuscaPagamentoUseCase buscaPagamentoUseCase;
    private final PagamentoWebMapper pagamentoWebMapper;

    @GetMapping
    public String listarPagamentos(@PageableDefault(size = 10) Pageable pageable, Model model) {
        var pagamentos = buscaPagamentoUseCase.buscarTodos(pageable);
        model.addAttribute("pagamentos", pagamentoWebMapper.toResponseList(pagamentos.getContent()));
        model.addAttribute("page", pagamentos);
        return "admin/pagamentos/lista";
    }

    @GetMapping("/{reference}")
    public String detalhesPagamento(@PathVariable String reference, Model model) {
        var pagamento = buscaPagamentoUseCase.buscarPorReferencia(reference)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado: " + reference));
        
        model.addAttribute("pagamento", pagamentoWebMapper.toResponse(pagamento));
        return "admin/pagamentos/detalhes";
    }
}
