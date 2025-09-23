package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.adapter.in.web.dto.CompradorInfoRequest;
import cv.ntinnovation.vintipay.adapter.in.web.mapper.IntencaoPagamentoWebMapper;
import cv.ntinnovation.vintipay.adapter.in.web.utils.CookieUtil;
import cv.ntinnovation.vintipay.application.core.domain.entity.*;
import cv.ntinnovation.vintipay.application.core.domain.enums.IntencaoPagamentoEnum;
import cv.ntinnovation.vintipay.application.core.domain.enums.MoedaEnum;
import cv.ntinnovation.vintipay.application.core.domain.enums.StatusIntencaoPagamentoEnum;
import cv.ntinnovation.vintipay.application.core.domain.enums.StatusPagamentoEnum;
import cv.ntinnovation.vintipay.application.core.domain.exception.IntencaoPagamentoNaoExisteExeption;
import cv.ntinnovation.vintipay.application.core.domain.exception.SessaoPagamentoInvalidaException;
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
    private final BuscaIntencaoPagamentoUseCase buscaIntencaoPagamentoUseCase;
    private final CriaSubscricaoPagamentoUseCase criaSubscricaoPagamentoUseCase;

    /**
     * Exibe a página de checkout para um link de pagamento
     */
    @GetMapping("/p/{codigo}")
    public String paginaCheckout(@PathVariable String codigo, @CookieValue(value = SESSION_COOKIE_NAME, required = false) String sessionId,
                                 @CookieValue(value = CLIENT_ID, required = false) String clientId, Model model, HttpServletRequest request, HttpServletResponse response) {
        if (clientId == null) {
            clientId = UUID.randomUUID().toString();
        }

        setClientIdCookie(response, clientId);

        Optional<SessaoPagamento> sessaoOpt = validaSessaoPagamentoUseCase.validarSessao(sessionId);

        if (sessaoOpt.isEmpty()) {
            sessionId = geraMerchantSession();
        }

        if (sessaoOpt.isPresent() && sessaoOpt.get().getDataExpiracao().isAfter(LocalDateTime.now().minusMinutes(20))){
            sessionId = geraMerchantSession();
        }

        if (sessaoOpt.isPresent()) {
            try {
                var intencaoPagamento = buscaIntencaoPagamentoUseCase.buscarPorReferencia(sessaoOpt.get().getReferencia());
                if (intencaoPagamento != null && !intencaoPagamento.getStatus().equals(StatusIntencaoPagamentoEnum.PENDENTE)) {
                    sessionId = geraMerchantSession();
                }
            } catch (IntencaoPagamentoNaoExisteExeption e) {
                sessionId = geraMerchantSession();
            }
        }

        Optional<LinkPagamento> linkOpt = gerenciaLinkPagamentoUseCase.buscarLinkPorCodigo(codigo);
        
        if (linkOpt.isEmpty() || !linkOpt.get().isDisponivel()) {
            model.addAttribute("erro", "Link de pagamento inválido ou expirado");
            return "links/erro";
        }
        
        LinkPagamento link = linkOpt.get();
        Produto produto = link.getProduto();
        
        // Verificar se o produto é recorrente e adicionar informações de subscrição ao modelo
        boolean isSubscription = produto.isRecorrente();
        model.addAttribute("isSubscription", isSubscription);
        
        if (isSubscription) {
            // Adicionar detalhes da recorrência ao modelo
            model.addAttribute("periodoRecorrencia", produto.getPeriodoRecorrencia());
            model.addAttribute("duracaoRecorrencia", produto.getDuracaoRecorrencia());
            model.addAttribute("descricaoRecorrencia", produto.getDescricaoRecorrencia());
            model.addAttribute("primeiraCobranca", produto.getPrimeiraCobranca());
            model.addAttribute("descontosPorCiclo", produto.getDescontosPorCiclo());
            
            log.info("Link de pagamento {} identificado como ASSINATURA: período {}, duração: {}", 
                     codigo, produto.getPeriodoRecorrencia(), 
                     produto.getDuracaoRecorrencia() != null ? produto.getDuracaoRecorrencia() + " ciclos" : "Ilimitada");
            var descontos = produto.getDescontosPorCiclo();

            if (descontos != null) {
                var desconto = descontos.stream().filter(d->d.getNumeroCiclo().equals(1)).findFirst();
                if (desconto.isPresent()){
                    var preco = produto.getPreco();
                    var descontado = preco.pow(desconto.get().getNumeroCiclo());
                    produto.setPreco(preco.subtract(descontado));
                }
            }
        }
        
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

        intencaoPagamento.setProduto(produto);

        intencaoPagamento.setCiclo(1);

        intencaoPagamento.gerarTimestamp();

        //Verificar se o produto é recorrente e adicionar informações de subscrição ao modelo
        boolean isSubscription = produto.isRecorrente();

        if (isSubscription) {
            var periodo = produto.getPeriodoRecorrencia();
            intencaoPagamento.setDataCobranca(periodo.getDataCobranca());

            var descontos = produto.getDescontosPorCiclo();

            if (descontos != null) {
                var desconto = descontos.stream().filter(d->d.getNumeroCiclo().equals(1)).findFirst();
                if (desconto.isPresent()){
                    var preco = produto.getPreco();
                    var descontado = preco.pow(desconto.get().getNumeroCiclo());
                    produto.setPreco(preco.subtract(descontado));
                }
            }

            intencaoPagamento.setIntencao(IntencaoPagamentoEnum.SUBSCRICAO);
            intencaoPagamento.setTransactionCode(IntencaoPagamentoEnum.SUBSCRICAO.getTransactionCode());

        }else {
            intencaoPagamento.setIntencao(IntencaoPagamentoEnum.COMPRAS);
            intencaoPagamento.setTransactionCode(IntencaoPagamentoEnum.COMPRAS.getTransactionCode());
        }

        intencaoPagamento.setValor(produto.getPreco());

        intencaoPagamento.setMoeda(MoedaEnum.getByName(produto.getMoeda()).getCodigoISO());

        // Definir o posId da configuração
        intencaoPagamento.setPosId(link.getConfiguracaoPagamento().getPosId());
        intencaoPagamento.setMerchantRef(sessaoOpt.get().getReferencia());
        intencaoPagamento.setMerchantSession(sessaoOpt.get().getSessionId());

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

        if (isSubscription) {
            return "redirect:/subscricao/checkout/" + intencaoPagamentoCriada.getReferencia();
        }else {
            return "redirect:/pagamento/checkout/" + intencaoPagamentoCriada.getReferencia();
        }
    }

    @GetMapping("/p/{clientId}/{referencia}")
    public String paginaCheckout(@PathVariable(value = "referencia") String referencia,
                                 @PathVariable(value = "clientId") String clientId, HttpServletResponse response, Model model) {

        var submitFormUrl = "/api/pagamento-publico/"+clientId+"/"+referencia;

        model.addAttribute("submitFormUrl", submitFormUrl);

        return "api/checkout";
    }

    @PostMapping("/p/{clientId}/{referencia}")
    public String paginap(@PathVariable(value = "referencia") String referencia,
                          @PathVariable(value = "clientId") String clientId, HttpServletResponse response) {

        setClientIdCookie(response, clientId);

        var intencaoPagamento = buscaIntencaoPagamentoUseCase.buscarPorReferencia(referencia);

        if(intencaoPagamento == null) {
            throw new IntencaoPagamentoNaoExisteExeption("Intencao de pagamento nao encontrada para a referência: " + referencia);
        }

        Optional<SessaoPagamento> sessaoOpt = validaSessaoPagamentoUseCase.validarSessao(intencaoPagamento.getMerchantSession());

        var sessao = sessaoOpt.orElseGet(() -> null);

        if (sessao == null) {
            throw new SessaoPagamentoInvalidaException("Sessão de pagamento inválida ou expirada para referência: " + referencia);
        }

        setSessionCookie(response, sessao.getSessionId());

        var isSubscription = intencaoPagamento.getIntencao().equals(IntencaoPagamentoEnum.SUBSCRICAO);

        if (isSubscription) {
            return "redirect:/subscricao/checkout/" + intencaoPagamento.getReferencia();
        }else {
            return "redirect:/pagamento/checkout/" + intencaoPagamento.getReferencia();
        }
    }

}
