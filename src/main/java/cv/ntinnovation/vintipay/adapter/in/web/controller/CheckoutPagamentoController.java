package cv.ntinnovation.vintipay.adapter.in.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cv.ntinnovation.vintipay.adapter.in.web.config.EnvironmentProperties;
import cv.ntinnovation.vintipay.adapter.in.web.dto.SispCheckoutRequest;
import cv.ntinnovation.vintipay.application.core.domain.entity.SessaoPagamento;
import cv.ntinnovation.vintipay.application.core.domain.enums.MoedaEnum;
import cv.ntinnovation.vintipay.application.core.domain.enums.StatusPagamentoEnum;
import cv.ntinnovation.vintipay.application.core.domain.exception.IntencaoPagamentoNaoExisteExeption;
import cv.ntinnovation.vintipay.application.core.domain.exception.PagamentoNaoEncontradoException;
import cv.ntinnovation.vintipay.application.core.domain.exception.PagamentoStatusNaoPendenteException;
import cv.ntinnovation.vintipay.application.core.domain.exception.SessaoPagamentoInvalidaException;
import cv.ntinnovation.vintipay.application.port.in.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

import static cv.ntinnovation.vintipay.adapter.in.web.utils.CookieUtil.CLIENT_ID;
import static cv.ntinnovation.vintipay.adapter.in.web.utils.CookieUtil.setSessionCookie;

@Slf4j
@Controller
@RequestMapping("/pagamento")
@RequiredArgsConstructor
public class CheckoutPagamentoController {

    private final CriaPagamentoUseCase criaPagamentoUseCase;
    private final BuscaPagamentoUseCase buscaPagamentoUseCase;
    private final ValidaSessaoPagamentoUseCase validaSessaoPagamentoUseCase;
    private final BuscaIntencaoPagamentoUseCase buscaIntencaoPagamentoUseCase;


    private final EnvironmentProperties environmentProperties;

    @GetMapping("/checkout/{reference}")
    public String checkout(@PathVariable String reference, Model model, HttpServletRequest request, HttpServletResponse servletResponse) {

        var intencaoPagamento = buscaIntencaoPagamentoUseCase.buscarPorReferencia(reference);

        if(intencaoPagamento == null) {
            throw new IntencaoPagamentoNaoExisteExeption("Intencao de pagamento nao encontrada para a referência: " + reference);
        }

        // Validar a sessão de pagamento
        Optional<SessaoPagamento> sessaoOpt = validaSessaoPagamentoUseCase.validarSessao(reference, request);
        
        if (sessaoOpt.isEmpty()) {
            log.warn("Tentativa de acesso ao checkout com sessão inválida para referência: {}", reference);
            throw new SessaoPagamentoInvalidaException("Sessão de pagamento inválida ou expirada para referência: " + reference);
        }
        
        log.info("Sessão válida para checkout da referência: {}, sessionId: {}", 
                reference, sessaoOpt.get().getSessionId());

        setSessionCookie(servletResponse, sessaoOpt.get().getSessionId());

        var pagamentoOptional = buscaPagamentoUseCase.buscarPorReferencia(reference);

        var pagamento = pagamentoOptional.orElseGet(() -> criaPagamentoUseCase.criar(intencaoPagamento));

        pagamento.gerarTimestamp();

        if (!pagamento.getStatus().equals(StatusPagamentoEnum.PENDENTE)){
            throw new PagamentoStatusNaoPendenteException("Pagamento está "+ pagamento.getStatus());
        }

        ObjectMapper objectMapper = new ObjectMapper();
        String purchaseRequest = "";
        try {
            purchaseRequest = objectMapper.writeValueAsString(pagamento.getIntencaoPagamento().getContaPagamento());
            purchaseRequest = Base64.getEncoder().encodeToString(purchaseRequest.getBytes());
        }catch (Exception e) {
            throw new RuntimeException(e);
        }

        var referenceNumber = pagamento.getIntencaoPagamento().getReferenceNumber() == null ? null : pagamento.getIntencaoPagamento().getReferenceNumber().toString();
        var entityCode = pagamento.getIntencaoPagamento().getEntityCode() == null ? null : pagamento.getIntencaoPagamento().getEntityCode();

        var submitFormUrl = intencaoPagamento.getAmbiente().getSubmitUrl(environmentProperties);

        var requestResponse = SispCheckoutRequest.builder()
            .timestamp(pagamento.getTimestamp())
            .purchaseRequest(purchaseRequest)
            .fingerprint(pagamento.getFingerprint())
            .fingerprintversion("1") // Versão fixa por enquanto
            .amount(pagamento.getIntencaoPagamento().getValor().toString())
            .transactionCode(pagamento.getIntencaoPagamento().getTransactionCode())
            .posID(pagamento.getIntencaoPagamento().getPosId())
            .merchantRef(pagamento.getIntencaoPagamento().getMerchantRef())
            .merchantSession(pagamento.getIntencaoPagamento().getMerchantSession())
            .currency(MoedaEnum.getByName(pagamento.getIntencaoPagamento().getMoeda()).getCodigoISO())
            .is3DSec("1") // Habilitado por padrão
            .urlMerchantResponse(String.format("%s/%s?fingerPrintCode=%s", environmentProperties.getReturnUrl(), pagamento.getIntencaoPagamento().getReferencia(), URLEncoder.encode(pagamento.getAuthorizationCode())))
            .languageMessages("pt") // Português por padrão
            .entityCode(entityCode)
            .referenceNumber(referenceNumber)
            .submitFormUrl(String.format("%s?FingerPrint=%s&TimeStamp=%s&FingerPrintVersion=%s",
                    submitFormUrl,
                    UriUtils.encode(pagamento.getFingerprint(), StandardCharsets.UTF_8),
                    UriUtils.encode(pagamento.getTimestamp(), StandardCharsets.UTF_8),
                    UriUtils.encode("1", StandardCharsets.UTF_8)))
            .build();

        model.addAttribute("pagamento", pagamento);
        model.addAttribute("request", requestResponse);
        model.addAttribute("sessao", sessaoOpt.get());

        return "checkout";
    }
}
