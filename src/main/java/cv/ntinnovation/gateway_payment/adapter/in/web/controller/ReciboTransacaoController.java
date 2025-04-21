package cv.ntinnovation.gateway_payment.adapter.in.web.controller;

import cv.ntinnovation.gateway_payment.application.core.domain.entity.Pagamento;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.SessaoPagamento;
import cv.ntinnovation.gateway_payment.application.core.domain.exception.PagamentoNaoEncontradoException;
import cv.ntinnovation.gateway_payment.application.core.domain.exception.SessaoPagamentoInvalidaException;
import cv.ntinnovation.gateway_payment.application.port.in.BuscaConfiguracaoPagamentoAtivaUseCase;
import cv.ntinnovation.gateway_payment.application.port.in.BuscaConfiguracaoPagamentoUseCase;
import cv.ntinnovation.gateway_payment.application.port.in.BuscaPagamentoUseCase;
import cv.ntinnovation.gateway_payment.application.port.in.ValidaSessaoPagamentoUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/pagamento")
@RequiredArgsConstructor
public class ReciboTransacaoController {

    private final BuscaPagamentoUseCase buscaPagamentoUseCase;
    private final BuscaConfiguracaoPagamentoUseCase buscaConfiguracaoPagamentoAtivaUseCase;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @GetMapping("/recibo/{reference}")
    public String exibirRecibo(
            @PathVariable String reference,
            Model model,
            HttpServletRequest request) {


        
        // Buscar o pagamento pela referência
        var pagamentoOptional = buscaPagamentoUseCase.buscarPorReferencia(reference);
        
        if (pagamentoOptional.isEmpty()) {
            throw new PagamentoNaoEncontradoException("Pagamento não encontrado para a referência: " + reference);
        }
        
        Pagamento pagamento = pagamentoOptional.get();
        
        // Buscar a configuração do comerciante
        var configuracao = buscaConfiguracaoPagamentoAtivaUseCase.buscarPorPosId(pagamento.getIntencaoPagamento().getPosId());
        
        // Adicionar os dados ao modelo
        model.addAttribute("pagamento", pagamento);
        model.addAttribute("configuracao", configuracao);
        model.addAttribute("dataFormatada", pagamento.getCreatedAt().format(DATE_FORMATTER));

        
        return "recibo";
    }
}
