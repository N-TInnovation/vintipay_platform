package cv.ntinnovation.vintipay.adapter.in.web.controller.api;

import cv.ntinnovation.vintipay.application.core.domain.entity.*;
import cv.ntinnovation.vintipay.application.core.domain.enums.IntencaoPagamentoEnum;
import cv.ntinnovation.vintipay.application.core.domain.exception.IntencaoPagamentoNaoExisteExeption;
import cv.ntinnovation.vintipay.application.core.domain.exception.SessaoPagamentoInvalidaException;
import cv.ntinnovation.vintipay.application.port.in.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static cv.ntinnovation.vintipay.adapter.in.web.utils.CookieUtil.*;

/**
 * Controller para gerenciar as páginas públicas de pagamento
 */
@Slf4j
@Controller
@RequestMapping
@RequiredArgsConstructor
public class PagamentoApiPublicoController {

    private final ValidaSessaoPagamentoUseCase validaSessaoPagamentoUseCase;
    private final BuscaIntencaoPagamentoUseCase buscaIntencaoPagamentoUseCase;

    /**
     * Exibe a página de checkout para um link de pagamento
     */
    @GetMapping("/api/pagamento-publico/{clientId}/{referencia}")
    public String paginaCheckout(@PathVariable(value = "referencia") String referencia,
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
