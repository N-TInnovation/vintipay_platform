package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.adapter.in.web.dto.CompradorInfoRequest;
import cv.ntinnovation.vintipay.adapter.in.web.mapper.IntencaoPagamentoWebMapper;
import cv.ntinnovation.vintipay.adapter.in.web.utils.CookieUtil;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static cv.ntinnovation.vintipay.adapter.in.web.utils.CookieUtil.*;
import static cv.ntinnovation.vintipay.adapter.in.web.utils.GerarUtil.geraMerchantRef;
import static cv.ntinnovation.vintipay.adapter.in.web.utils.GerarUtil.geraMerchantSession;

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


    /**
     * Exibe a página de checkout para um link de pagamento
     */
    @GetMapping("/p/{codigo}")
    public String paginaCheckout(@PathVariable String codigo, @CookieValue(value = SESSION_COOKIE_NAME, required = false) String sessionId,
                                 @CookieValue(value = CLIENT_ID, required = false) String clientId, Model model, HttpServletRequest request, HttpServletResponse response) {
        if (clientId == null) {
            clientId = UUID.randomUUID().toString();
        }

        Optional<SessaoPagamento> sessaoOpt = validaSessaoPagamentoUseCase.validarSessao(sessionId);

        if (sessaoOpt.isEmpty()) {
            sessionId = geraMerchantSession();
        }

        if (sessaoOpt. isPresent() && sessaoOpt.get().getDataExpiracao().isAfter(LocalDateTime.now().minusMinutes(10))){
            sessionId = geraMerchantSession();
        }


        setClientIdCookie(response, clientId);

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
        
        model.addAttribute("compradorInfoRequest", compradorInfo);

        // Criar uma sessão de pagamento para esta intenção
        SessaoPagamento sessao = criaSessaoPagamentoUseCase.criar(
                geraMerchantRef(),
                sessionId,
                clientId,
                request
        );

        log.info("Sessão criada para o link {}, e id da sessão: {}", codigo, sessao.getSessionId());

        setSessionCookie(response, sessao.getSessionId());

        return "links/checkout";
    }

    /**
     * Processa o pagamento de um link
     */
    @PostMapping("/p/{codigo}")
    public String processarPagamento(
            @PathVariable String codigo,
            @Valid @ModelAttribute("compradorInfoRequest") CompradorInfoRequest compradorInfo,
            @CookieValue(value = SESSION_COOKIE_NAME, required = false) String sessionId,
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
        intencaoPagamento.setMerchantRef(sessaoOpt.get().getReferencia());
        intencaoPagamento.setMerchantSession(sessaoOpt.get().getSessionId());


        intencaoPagamento.setTransactionCode(IntencaoPagamentoEnum.COMPRAS.getTransactionCode());

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

        return "redirect:/pagamento/checkout/" + intencaoPagamentoCriada.getReferencia();
    }



}
