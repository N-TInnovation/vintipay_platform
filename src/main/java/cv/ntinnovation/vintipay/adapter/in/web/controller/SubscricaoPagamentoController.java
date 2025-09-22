package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.adapter.in.web.config.EnvironmentProperties;
import cv.ntinnovation.vintipay.adapter.in.web.dto.SispCheckoutRequest;
import cv.ntinnovation.vintipay.application.core.domain.entity.SessaoPagamento;
import cv.ntinnovation.vintipay.application.core.domain.enums.MoedaEnum;
import cv.ntinnovation.vintipay.application.core.domain.exception.IntencaoPagamentoNaoExisteExeption;
import cv.ntinnovation.vintipay.application.core.domain.exception.SessaoPagamentoInvalidaException;
import cv.ntinnovation.vintipay.application.port.in.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static cv.ntinnovation.vintipay.adapter.in.web.utils.CookieUtil.setSessionCookie;

@Slf4j
@Controller
@RequestMapping("/subscricao")
@RequiredArgsConstructor
public class SubscricaoPagamentoController {

    private final CriaPagamentoUseCase criaPagamentoUseCase;
    private final BuscaPagamentoUseCase buscaPagamentoUseCase;
    private final ValidaSessaoPagamentoUseCase validaSessaoPagamentoUseCase;
    private final BuscaIntencaoPagamentoUseCase buscaIntencaoPagamentoUseCase;
    private final CriaSubscricaoPagamentoUseCase criaSubscricaoPagamentoUseCase;


    private final EnvironmentProperties environmentProperties;

    @GetMapping("/checkout/{reference}")
    public String checkoutComprasRecargas(@PathVariable String reference, Model model, HttpServletRequest request, HttpServletResponse servletResponse) {

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

        var subscricaoPagamento = criaSubscricaoPagamentoUseCase.subscrever(intencaoPagamento);

        var submitFormUrl = intencaoPagamento.getAmbiente().getSubmitSubscricaoUrl(environmentProperties);

        subscricaoPagamento.setAuthorizationCode("");

        var requestResponse = SispCheckoutRequest.builder()
                .timestamp(intencaoPagamento.getTimestamp())
                .purchaseRequest(null)
                .fingerprint(subscricaoPagamento.getFingerprint())
                .fingerprintversion("1") // Versão fixa por enquanto
                .amount(intencaoPagamento.getValor().toString())
                .transactionCode(intencaoPagamento.getTransactionCode())
                .posID(intencaoPagamento.getPosId())
                .merchantRef(intencaoPagamento.getMerchantRef())
                .merchantSession(intencaoPagamento.getMerchantSession())
                .currency(MoedaEnum.getByName(intencaoPagamento.getMoeda()).getCodigoISO())
                .is3DSec("1") // Habilitado por padrão
                .urlMerchantResponse(String.format("%s/%s?fingerPrintCode=%s", environmentProperties.getReturnUrl(), subscricaoPagamento.getReference(), URLEncoder.encode(subscricaoPagamento.getAuthorizationCode())))
                .languageMessages("pt") // Português por padrão
                .entityCode(null)
                .referenceNumber(null)
                .submitFormUrl(String.format("%s?FingerPrint=%s&TimeStamp=%s&FingerPrintVersion=%s",
                        submitFormUrl,
                        UriUtils.encode(subscricaoPagamento.getFingerprint(), StandardCharsets.UTF_8),
                        UriUtils.encode(intencaoPagamento.getTimestamp(), StandardCharsets.UTF_8),
                        UriUtils.encode("1", StandardCharsets.UTF_8)))
                .build();

        model.addAttribute("request", requestResponse);
        model.addAttribute("sessao", sessaoOpt.get());

        return "checkout";
    }
}
