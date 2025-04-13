package cv.ntinnovation.gateway_payment.adapter.in.web.controller;

import cv.ntinnovation.gateway_payment.adapter.in.web.dto.SispWebhookRequest;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.ConfiguracaoPagamento;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.Pagamento;
import cv.ntinnovation.gateway_payment.application.core.domain.enums.MensagemRetornoEnum;
import cv.ntinnovation.gateway_payment.application.port.in.AtualizaStatusPagamentoUseCase;
import cv.ntinnovation.gateway_payment.application.port.in.BuscaConfiguracaoPagamentoUseCase;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class WebhookPagamentoController {

    private final AtualizaStatusPagamentoUseCase atualizaStatusPagamentoUseCase;
    private final BuscaConfiguracaoPagamentoUseCase buscaConfiguracaoPagamentoUseCase;

    @PostMapping(path = "/sisp/pagamentos/retorno/{reference}", consumes = {"application/x-www-form-urlencoded;charset=UTF-8"}, produces = {MediaType.ALL_VALUE})
    public ModelAndView handlePaymentWebhook(@PathVariable String reference,
                                             @RequestBody SispWebhookRequest request,
                                             HttpServletRequest servletRequest) throws IOException {
        log.info("Recebido webhook do SISP: {}", request);

        try {
            MensagemRetornoEnum mensagemRetornoEnum = MensagemRetornoEnum.getMensagemRetornoEnum(
                    request.getMessageType(),
                    request.getMerchantResp(),
                    request.getUserCancelled());

            String mensagem = getMensagemRetorno(mensagemRetornoEnum, request);
            
            Pagamento pagamento = atualizaStatusPagamentoUseCase.atualizar(
                    reference,
                    mensagemRetornoEnum.getStatus(),
                    mensagem,
                    request.getResultFingerPrint(),
                    request.getMerchantRespPan()
            );

            ConfiguracaoPagamento configuracaoPagamento = buscaConfiguracaoPagamentoUseCase.buscarPorPosId(pagamento.getIntencaoPagamento().getPosId());

            // Redireciona para a URL de retorno da configuração com os parâmetros necessários
            String redirectUrl = String.format("%s?status=%s&message=%s",
                    configuracaoPagamento.getReturnUrl(),
                    pagamento.getStatus(),
                    mensagem);

            servletRequest.setAttribute(
                    View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
            return new ModelAndView("redirect:" + redirectUrl);
        } catch (Exception e) {
            log.error("Erro ao processar webhook do SISP", e);
            throw new RuntimeException( "Erro ao processar pagamento");
        }
    }

    private String getMensagemRetorno(MensagemRetornoEnum mensagemRetornoEnum, SispWebhookRequest request) {
        String merchantRespErrorDescription = request.getMerchantRespErrorDescription();
        String merchantRespErrorDetail = request.getMerchantRespErrorDetail();
        return mensagemRetornoEnum.equals(MensagemRetornoEnum.TRANSACAO_FALHA) ?
                merchantRespErrorDescription + " - " + merchantRespErrorDetail :
                mensagemRetornoEnum.getDescricao();
    }
}
