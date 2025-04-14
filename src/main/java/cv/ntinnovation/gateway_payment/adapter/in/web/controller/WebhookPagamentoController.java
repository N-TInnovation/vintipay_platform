package cv.ntinnovation.gateway_payment.adapter.in.web.controller;

import cv.ntinnovation.gateway_payment.adapter.in.web.dto.CheckoutResponseDto;
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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class WebhookPagamentoController {

    private final AtualizaStatusPagamentoUseCase atualizaStatusPagamentoUseCase;
    private final BuscaConfiguracaoPagamentoUseCase buscaConfiguracaoPagamentoUseCase;

    @RequestMapping(value = "/sisp/pagamentos/retorno/{reference}", method = RequestMethod.POST, consumes = {"application/x-www-form-urlencoded;charset=UTF-8"}, produces = {MediaType.ALL_VALUE})
    public String handlePaymentWebhook(@PathVariable String reference,
                                     @RequestBody SispWebhookRequest request,
                                     Model model) {
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

            // Monta o objeto de resposta
            CheckoutResponseDto responseDto = CheckoutResponseDto.builder()
                    .reference(pagamento.getIntencaoPagamento().getMerchantRef())
                    .status(pagamento.getStatus())
                    .message(mensagem)
                    .amount(pagamento.getIntencaoPagamento().getAmount())
                    .currency(pagamento.getIntencaoPagamento().getCurrency())
                    .merchantSession(pagamento.getIntencaoPagamento().getMerchantSession())
                    .panMascarado(pagamento.getPanMascarado())
                    .merchantRef(pagamento.getIntencaoPagamento().getMerchantRef())
                    .build();

            model.addAttribute("response", responseDto);
            model.addAttribute("submitFormUrl", configuracaoPagamento.getReturnUrl());

            return "checkout-response.html";
        } catch (Exception e) {
            log.error("Erro ao processar webhook do SISP", e);
            throw new RuntimeException("Erro ao processar pagamento");
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
