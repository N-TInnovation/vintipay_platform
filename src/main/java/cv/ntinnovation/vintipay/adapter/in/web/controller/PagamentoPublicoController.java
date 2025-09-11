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

import java.time.LocalDate;
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
    private final ValidaSessaoPagamentoUseCase validaSessaoPagamentoUseCase;

    private static final String SESSION_COOKIE_NAME = "PAYMENT_SESSION_ID";
    private static final String CLIENT_ID = "CLIENT_ID";
    private static final int SESSION_COOKIE_MAX_AGE = 1800; // 30 minutos em segundos
    private static final int CLIENT_ID_MAX_AGE = 86400; // 40 dias em segundos

    /**
     * Exibe a página de checkout para um link de pagamento
     */
    @GetMapping("/p/{codigo}")
    public String paginaCheckout(@PathVariable String codigo, @CookieValue(value = SESSION_COOKIE_NAME, required = false) String sessionId,
                                 @CookieValue(value = CLIENT_ID, required = false) String clientId, Model model, HttpServletRequest request, HttpServletResponse response) {
        if (clientId == null) {
            clientId = UUID.randomUUID().toString();
        }

        // Adicionar cookie de sessão na resposta

        Cookie clientIdCookie = new Cookie(CLIENT_ID, clientId);
        clientIdCookie.setMaxAge(CLIENT_ID_MAX_AGE);
        clientIdCookie.setPath("/");
        clientIdCookie.setHttpOnly(true);
        clientIdCookie.setSecure(true); // Somente HTTPS
        response.addCookie(clientIdCookie);

        if (sessionId != null) {
            Optional<SessaoPagamento> sessaoOpt = validaSessaoPagamentoUseCase.validarSessao(sessionId);

            if (sessaoOpt.isPresent()) {
                validaSessaoPagamentoUseCase.invalidarSessao(sessionId);
            }
        }

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

        // Criar uma sessão de pagamento para esta intenção
        SessaoPagamento sessao = criaSessaoPagamentoUseCase.criar(
                UUID.randomUUID().toString(),
                clientId,
                request
        );

        // Adicionar cookie de sessão na resposta
        Cookie sessionCookie = new Cookie(SESSION_COOKIE_NAME, sessao.getSessionId());
        sessionCookie.setMaxAge(SESSION_COOKIE_MAX_AGE);
        sessionCookie.setPath("/");
        sessionCookie.setHttpOnly(true);
        sessionCookie.setSecure(true); // Somente HTTPS
        response.addCookie(sessionCookie);

        
        return "links/checkout";
    }

    /**
     * Processa o pagamento de um link
     */
    @PostMapping("/p/{codigo}")
    public String processarPagamento(
            @PathVariable String codigo,
            @Valid @ModelAttribute("compradorInfoRequest") CompradorInfoRequest compradorInfo,
            @CookieValue(value = SESSION_COOKIE_NAME) String sessionId,
            @CookieValue(value = CLIENT_ID, required = false) String clientId,
            BindingResult bindingResult,
            Model model,
            HttpServletRequest request,
            HttpServletResponse response) {

        Optional<SessaoPagamento> sessaoOpt = validaSessaoPagamentoUseCase.validarSessao(sessionId);

        if (sessaoOpt.isEmpty()) {
            model.addAttribute("erro", "Sessão inválida");
            return "links/erro";
        }


        Optional<LinkPagamento> linkOpt = gerenciaLinkPagamentoUseCase.buscarLinkPorCodigo(codigo);
        
        if (linkOpt.isEmpty() || !linkOpt.get().isDisponivel()) {
            model.addAttribute("erro", "Link de pagamento inválido ou expirado");
            return "links/erro";
        }

        LinkPagamento link = linkOpt.get();
        Produto produto = link.getProduto();

        // Se houver erros de validação, retornar para a página de checkout
        if (bindingResult.hasErrors()) {
            model.addAttribute("link", link);
            model.addAttribute("produto", produto);
            model.addAttribute("codigo", codigo);
            return "links/checkout";
        }
        
        log.info("Processando pagamento para código: {}, email: {}, cidade: {}", 
                codigo, compradorInfo.getEmail(), compradorInfo.getBillAddrCity());

        IntencaoPagamento intencaoPagamento = intencaoPagamentoWebMapper.toIntencaoPagamento(compradorInfo);

        intencaoPagamento.setReferencia(sessaoOpt.get().getReferencia());

        intencaoPagamento.setValor(produto.getPreco());

        intencaoPagamento.setMoeda(MoedaEnum.getByName(produto.getMoeda()).getCodigoISO());

        // Definir o posId da configuração
        intencaoPagamento.setPosId(link.getConfiguracaoPagamento().getPosId());
        intencaoPagamento.setMerchantRef(getMerchantRef(produto.getId().toString()));
        intencaoPagamento.setMerchantSession(getMerchantSession(link.getCodigo()));

        intencaoPagamento.setTransactionCode(IntencaoPagamentoEnum.COMPRAS.getTransactionCode());
        log.info("Ambiente: {}",
                link.getConfiguracaoPagamento().getAmbiente());
        intencaoPagamento.setAmbiente(link.getConfiguracaoPagamento().getAmbiente());

        // Criar ou buscar a conta de pagamento pelo email
        ContaPagamento contaPagamento = criaContaPagamentoUseCase.criarOuBuscarPorEmail(
                compradorInfo.getName(),
                compradorInfo.getEmail(),
                compradorInfo.getBillAddrCity(),
                compradorInfo.getBillAddrState(),
                compradorInfo.getBillAddrCountry(),
                compradorInfo.getBillAddrLine1(),
                compradorInfo.getBillAddrLine2(),
                compradorInfo.getBillAddrPostCode(),
                compradorInfo.getPhoneCode(),
                compradorInfo.getPhoneNumber()
        );

        // Associar a conta de pagamento à intenção
        intencaoPagamento.setContaPagamento(contaPagamento);
        // Criar a intenção de pagamento
        IntencaoPagamento intencaoPagamentoCriada = criaIntencaoPagamentoUseCase.criar(intencaoPagamento);


        gerenciaLinkPagamentoUseCase.incrementarUsos(link.getId());

        //model.addAttribute("sessionId", sessao.getSessionId());
        return "redirect:/pagamento/checkout/" + intencaoPagamentoCriada.getReferencia();
    }

    private String getMerchantRef(String merchantRef){
        return "R"+ LocalDate.now().toString()+merchantRef;
    }

    private String getMerchantSession(String merchantSession){
        return "S"+ LocalDate.now().toString()+merchantSession;
    }

}
