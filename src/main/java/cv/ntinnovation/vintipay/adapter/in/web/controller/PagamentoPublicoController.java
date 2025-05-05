package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.adapter.in.web.dto.CompradorInfoRequest;
import cv.ntinnovation.vintipay.application.core.domain.entity.LinkPagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.Produto;
import cv.ntinnovation.vintipay.application.port.in.GerenciaLinkPagamentoUseCase;
import cv.ntinnovation.vintipay.application.port.in.ProcessamentoPagamentoLinkUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;

/**
 * Controller para gerenciar as páginas públicas de pagamento
 */
@Slf4j
@Controller
@RequestMapping
@RequiredArgsConstructor
public class PagamentoPublicoController {

    private final GerenciaLinkPagamentoUseCase gerenciaLinkPagamentoUseCase;
    private final ProcessamentoPagamentoLinkUseCase processamentoPagamentoLinkUseCase;

    /**
     * Exibe a página de checkout para um link de pagamento
     */
    @GetMapping("/p/{codigo}")
    public String paginaCheckout(@PathVariable String codigo, Model model) {
        Optional<LinkPagamento> linkOpt = gerenciaLinkPagamentoUseCase.buscarLinkPorCodigo(codigo);
        
        if (linkOpt.isEmpty() || !linkOpt.get().isDisponivel()) {
            model.addAttribute("erro", "Link de pagamento inválido ou expirado");
            return "links/erro";
        }
        
        LinkPagamento link = linkOpt.get();
        Produto produto = link.getProduto();
        
        model.addAttribute("link", link);
        model.addAttribute("produto", produto);
        model.addAttribute("codigo", codigo);
        
        // Adicionar o objeto CompradorInfoRequest vazio ao modelo
        CompradorInfoRequest compradorInfo = new CompradorInfoRequest();
        compradorInfo.setPhoneCode("238"); // Código padrão para Cabo Verde
        compradorInfo.setBillAddrCountry("132"); // País padrão
        
        // Gerar um merchantSession único
        compradorInfo.setMerchantSession(UUID.randomUUID().toString());
        
        model.addAttribute("compradorInfoRequest", compradorInfo);

        
        return "links/checkout";
    }

    /**
     * Processa o pagamento de um link
     */
    @PostMapping("/p/{codigo}")
    public String processarPagamento(
            @PathVariable String codigo, 
            @Valid @ModelAttribute("compradorInfoRequest") CompradorInfoRequest compradorInfo,
            BindingResult bindingResult,
            Model model,
            HttpServletRequest request) {
        
        Optional<LinkPagamento> linkOpt = gerenciaLinkPagamentoUseCase.buscarLinkPorCodigo(codigo);
        
        if (linkOpt.isEmpty() || !linkOpt.get().isDisponivel()) {
            model.addAttribute("erro", "Link de pagamento inválido ou expirado");
            return "links/erro";
        }
        
        // Se houver erros de validação, retornar para a página de checkout
        if (bindingResult.hasErrors()) {
            LinkPagamento link = linkOpt.get();
            Produto produto = link.getProduto();
            
            model.addAttribute("link", link);
            model.addAttribute("produto", produto);
            model.addAttribute("codigo", codigo);
            
            return "links/checkout";
        }
        
        // Construir URLs de sucesso e cancelamento
        String baseUrl = request.getScheme() + "://" + request.getServerName();
        if (request.getServerPort() != 80 && request.getServerPort() != 443) {
            baseUrl += ":" + request.getServerPort();
        }
        
        String successUrl = baseUrl + "/p/" + codigo + "/sucesso";
        String cancelUrl = baseUrl + "/p/" + codigo + "/cancelado";
        
        log.info("Processando pagamento para código: {}, email: {}, cidade: {}", 
                codigo, compradorInfo.getEmail(), compradorInfo.getBillAddrCity());
        
        // Iniciar sessão de pagamento com os dados do comprador
        String redirectUrl = processamentoPagamentoLinkUseCase.criarSessaoPagamento(
            codigo, 
            successUrl, 
            cancelUrl,
            compradorInfo.getEmail(),
            compradorInfo.getBillAddrCity(),
            compradorInfo.getBillAddrCountry(),
            compradorInfo.getBillAddrLine1(),
            compradorInfo.getBillAddrPostCode(),
            compradorInfo.getMerchantSession(),
            compradorInfo.getPhoneCode(),
            compradorInfo.getPhoneNumber()
        );
        
        return "redirect:" + redirectUrl;
    }

    /**
     * Exibe a página de sucesso após o pagamento
     */
    @GetMapping("/p/{codigo}/sucesso")
    public String paginaSucesso(@PathVariable String codigo, 
                               @RequestParam String sessionId,
                               Model model) {
        Optional<LinkPagamento> linkOpt = gerenciaLinkPagamentoUseCase.buscarLinkPorCodigo(codigo);
        
        if (linkOpt.isEmpty()) {
            return "redirect:/p/" + codigo + "?erro=link_invalido";
        }
        
        LinkPagamento link = linkOpt.get();
        Produto produto = link.getProduto();
        
        // Verificar status do pagamento
        boolean pagamentoConfirmado = processamentoPagamentoLinkUseCase.processarPagamentoSucesso(sessionId, codigo);
        
        model.addAttribute("link", link);
        model.addAttribute("produto", produto);
        model.addAttribute("pagamentoConfirmado", pagamentoConfirmado);
        
        return "links/sucesso";
    }
    
    /**
     * Exibe a página de cancelamento após o pagamento ser cancelado
     */
    @GetMapping("/p/{codigo}/cancelado")
    public String paginaCancelamento(@PathVariable String codigo, Model model) {
        Optional<LinkPagamento> linkOpt = gerenciaLinkPagamentoUseCase.buscarLinkPorCodigo(codigo);
        
        if (linkOpt.isEmpty()) {
            return "redirect:/p/" + codigo + "?erro=link_invalido";
        }
        
        LinkPagamento link = linkOpt.get();
        Produto produto = link.getProduto();
        
        model.addAttribute("link", link);
        model.addAttribute("produto", produto);
        
        return "links/cancelado";
    }
}
