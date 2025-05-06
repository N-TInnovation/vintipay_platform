package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.adapter.in.web.dto.CompradorInfoRequest;
import cv.ntinnovation.vintipay.adapter.in.web.mapper.IntencaoPagamentoWebMapper;
import cv.ntinnovation.vintipay.application.core.domain.entity.*;
import cv.ntinnovation.vintipay.application.core.domain.enums.IntencaoPagamentoEnum;
import cv.ntinnovation.vintipay.application.core.domain.enums.MoedaEnum;
import cv.ntinnovation.vintipay.application.port.in.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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
    private final CriaIntencaoPagamentoUseCase criaIntencaoPagamentoUseCase;
    private final CriaContaPagamentoUseCase criaContaPagamentoUseCase;
    private final IntencaoPagamentoWebMapper intencaoPagamentoWebMapper;
    private final CriaSessaoPagamentoUseCase criaSessaoPagamentoUseCase;

    private static final String SESSION_COOKIE_NAME = "PAYMENT_SESSION_ID";
    private static final int SESSION_COOKIE_MAX_AGE = 1800; // 30 minutos em segundos

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
            HttpServletRequest request,
            HttpServletResponse response) {
        
        Optional<LinkPagamento> linkOpt = gerenciaLinkPagamentoUseCase.buscarLinkPorCodigo(codigo);
        
        if (linkOpt.isEmpty() || !linkOpt.get().isDisponivel()) {
            model.addAttribute("erro", "Link de pagamento inválido ou expirado");
            return "links/erro";
        }

        LinkPagamento link = linkOpt.get();
        Produto produto = link.getProduto();

        // Se houver erros de validação, retornar para a página de checkout
        if (bindingResult.hasErrors()) {
            System.out.println("erros ");
            System.out.println(bindingResult.getModel());

            
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

        IntencaoPagamento intencaoPagamento = intencaoPagamentoWebMapper.toIntencaoPagamento(compradorInfo);

        intencaoPagamento.setValor(produto.getPreco());

        intencaoPagamento.setMoeda(MoedaEnum.getByName(produto.getMoeda()).getCodigoISO());

        // Definir o posId da configuração
        intencaoPagamento.setPosId(link.getConfiguracaoPagamento().getPosId());
        intencaoPagamento.setMerchantRef(produto.getId().toString());
        intencaoPagamento.setMerchantSession(link.getCodigo());

        intencaoPagamento.setTransactionCode(IntencaoPagamentoEnum.COMPRAS.getTransactionCode());

        // Criar ou buscar a conta de pagamento pelo email
        ContaPagamento contaPagamento = criaContaPagamentoUseCase.criarOuBuscarPorEmail(
                compradorInfo.getEmail(),
                compradorInfo.getBillAddrCity(),
                compradorInfo.getBillAddrCountry(),
                compradorInfo.getBillAddrLine1(),
                compradorInfo.getBillAddrPostCode(),
                compradorInfo.getPhoneCode(),
                compradorInfo.getPhoneNumber()
        );

        // Associar a conta de pagamento à intenção
        intencaoPagamento.setContaPagamento(contaPagamento);

        // Criar a intenção de pagamento
        IntencaoPagamento intencaoPagamentoCriada = criaIntencaoPagamentoUseCase.criar(intencaoPagamento);

        // Criar uma sessão de pagamento para esta intenção
        SessaoPagamento sessao = criaSessaoPagamentoUseCase.criar(
                intencaoPagamentoCriada.getReferencia(),
                link.getConfiguracaoPagamento().getApiKey(),
                request
        );

        // Adicionar cookie de sessão na resposta
        Cookie sessionCookie = new Cookie(SESSION_COOKIE_NAME, sessao.getSessionId());
        sessionCookie.setMaxAge(SESSION_COOKIE_MAX_AGE);
        sessionCookie.setPath("/");
        sessionCookie.setHttpOnly(true);
        sessionCookie.setSecure(true); // Somente HTTPS
        response.addCookie(sessionCookie);

        gerenciaLinkPagamentoUseCase.incrementarUsos(link.getId());

        model.addAttribute("referencia", intencaoPagamentoCriada.getReferencia());

        return "links/redirect";

    }

}
