package cv.ntinnovation.gateway_payment.adapter.in.web.controller;

import cv.ntinnovation.gateway_payment.adapter.in.web.dto.CheckoutResponseDto;
import cv.ntinnovation.gateway_payment.adapter.in.web.dto.SispWebhookRequest;
import cv.ntinnovation.gateway_payment.adapter.in.web.mapper.WebhookPagamentoMapper;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.ConfiguracaoPagamento;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.Pagamento;
import cv.ntinnovation.gateway_payment.application.core.domain.enums.MensagemRetornoEnum;
import cv.ntinnovation.gateway_payment.application.port.in.AtualizaStatusPagamentoUseCase;
import cv.ntinnovation.gateway_payment.application.port.in.BuscaConfiguracaoPagamentoUseCase;
import cv.ntinnovation.gateway_payment.application.port.in.ValidaRetornoUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class WebhookPagamentoController {

    private final AtualizaStatusPagamentoUseCase atualizaStatusPagamentoUseCase;
    private final BuscaConfiguracaoPagamentoUseCase buscaConfiguracaoPagamentoUseCase;
    private final ValidaRetornoUseCase validaRetornoUseCase;
    private final WebhookPagamentoMapper webhookPagamentoMapper;

    @PostMapping(value = "/sisp/pagamentos/retorno/{reference}",
            consumes={"application/x-www-form-urlencoded;charset=UTF-8"}, produces={ MediaType.ALL_VALUE })
    public String handlePaymentWebhook(@PathVariable String reference,
                                     @ModelAttribute SispWebhookRequest request,
                                     Model model) {
        log.info("Recebido webhook do SISP - Reference: {}, Request: {}", reference, request);

        try {

            var retornoFingerprint = webhookPagamentoMapper.toRetornoFingerprint(request);

            validaRetornoUseCase.executar(retornoFingerprint);

            log.debug("Processando mensagem de retorno");
            MensagemRetornoEnum mensagemRetornoEnum = MensagemRetornoEnum.getMensagemRetornoEnum(
                    request.getMessageType(),
                    request.getMerchantResp(),
                    request.getUserCancelled());

            String mensagem = getMensagemRetorno(mensagemRetornoEnum, request);
            log.debug("Mensagem de retorno: {}", mensagem);
            
            log.debug("Atualizando status do pagamento");
            Pagamento pagamento = atualizaStatusPagamentoUseCase.atualizar(
                    reference,
                    mensagemRetornoEnum.getStatus(),
                    mensagem,
                    request.getResultFingerPrint(),
                    request.getMerchantRespPan()
            );
            log.debug("Pagamento atualizado: {}", pagamento);

            log.debug("Buscando configuração de pagamento");
            ConfiguracaoPagamento configuracaoPagamento = buscaConfiguracaoPagamentoUseCase.buscarPorPosId(pagamento.getIntencaoPagamento().getPosId());
            log.debug("Configuração encontrada: {}", configuracaoPagamento);

            CheckoutResponseDto responseDto = CheckoutResponseDto.builder()
                    .reference(pagamento.getReference())
                    .status(pagamento.getStatus().getStatus())
                    .message(mensagem)
                    .amount(pagamento.getIntencaoPagamento().getAmount())
                    .currency(pagamento.getIntencaoPagamento().getCurrency())
                    .merchantSession(pagamento.getIntencaoPagamento().getMerchantSession())
                    .panMascarado(pagamento.getPanMascarado())
                    .merchantRef(pagamento.getIntencaoPagamento().getMerchantRef())
                    .timestamp(pagamento.getTimestamp())
                    .build();
            log.debug("Response DTO criado: {}", responseDto);

            model.addAttribute("response", responseDto);
            model.addAttribute("submitFormUrl", configuracaoPagamento.getReturnUrl());
            log.info("Redirecionando para: {}", configuracaoPagamento.getReturnUrl());

            return "checkout-response";
        } catch (Exception e) {
            log.error("Erro ao processar webhook do SISP", e);
            throw new RuntimeException("Erro ao processar pagamento", e);
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
