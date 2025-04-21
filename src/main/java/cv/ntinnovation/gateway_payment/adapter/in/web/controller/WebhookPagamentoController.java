package cv.ntinnovation.gateway_payment.adapter.in.web.controller;

import cv.ntinnovation.gateway_payment.adapter.in.web.dto.CheckoutResponseDto;
import cv.ntinnovation.gateway_payment.adapter.in.web.dto.SispWebhookRequest;
import cv.ntinnovation.gateway_payment.adapter.in.web.mapper.WebhookPagamentoMapper;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.ConfiguracaoPagamento;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.IntencaoPagamento;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.Pagamento;
import cv.ntinnovation.gateway_payment.application.core.domain.enums.MensagemRetornoEnum;
import cv.ntinnovation.gateway_payment.application.core.domain.exception.PagamentoNaoEncontradoException;
import cv.ntinnovation.gateway_payment.application.core.domain.rules.FingerPrintService;
import cv.ntinnovation.gateway_payment.application.port.in.*;
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
    private final BuscaPagamentoUseCase buscaPagamentoUseCase;
    private final ValidaRetornoUseCase validaRetornoUseCase;
    private final WebhookPagamentoMapper webhookPagamentoMapper;
    private final FingerPrintService fingerPrintService;

    @PostMapping(value = "/sisp/pagamentos/retorno/{reference}",
            consumes={"application/x-www-form-urlencoded;charset=UTF-8"}, produces={ MediaType.ALL_VALUE })
    public String handlePaymentWebhook(@PathVariable("reference") String reference,
                                     @RequestParam("fingerPrintCode") String authorization,
                                     @ModelAttribute SispWebhookRequest request,
                                     Model model) {
        log.info("Recebido webhook do SISP - Reference: {}, Request: {}", reference, request);

        var pagamentoExistente = buscaPagamentoUseCase.buscarPorReferencia(reference);

        if (pagamentoExistente.isPresent()) {

            var retornoFingerprint = webhookPagamentoMapper.toRetornoFingerprint(request);

            retornoFingerprint.setPagamento(pagamentoExistente.get());

            retornoFingerprint.setAuthorizationCode(authorization);

            ConfiguracaoPagamento configuracaoPagamento = buscaConfiguracaoPagamentoUseCase.buscarPorPosId(pagamentoExistente.map(Pagamento::getIntencaoPagamento).orElse(IntencaoPagamento.builder().build()).getPosId());
            retornoFingerprint.setPosAutCode(configuracaoPagamento.getPosAutCode());

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
            
            // Gerar HMAC para verificação de integridade dos dados
            String hmacFingerprint = fingerPrintService.generateCheckoutResponseHmac(
                responseDto.getReference(),
                responseDto.getStatus(),
                responseDto.getMessage(),
                responseDto.getAmount(),
                responseDto.getCurrency(),
                responseDto.getMerchantSession(),
                responseDto.getMerchantRef(),
                responseDto.getTimestamp(),
                responseDto.getPanMascarado(),
                configuracaoPagamento.getReturnAutCode() // Usando returnAutCode como chave secreta
            );
            
            log.debug("HMAC Fingerprint gerado: {}", hmacFingerprint);

            model.addAttribute("response", responseDto);
            model.addAttribute("submitFormUrl", configuracaoPagamento.getReturnUrl());
            model.addAttribute("hmacFingerprint", hmacFingerprint);
            log.info("Redirecionando para: {}", configuracaoPagamento.getReturnUrl());

            return "checkout-response";
        } else {
            throw new PagamentoNaoEncontradoException("Pagamento não encontrado");
        }

    }

    private String getMensagemRetorno(MensagemRetornoEnum mensagemRetornoEnum, SispWebhookRequest request) {
        return mensagemRetornoEnum.getDescricao();
    }
}
