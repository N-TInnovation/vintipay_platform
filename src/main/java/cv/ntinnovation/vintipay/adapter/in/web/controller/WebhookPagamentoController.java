package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.adapter.in.web.dto.CheckoutResponseDto;
import cv.ntinnovation.vintipay.adapter.in.web.dto.SispWebhookRequest;
import cv.ntinnovation.vintipay.adapter.in.web.mapper.WebhookPagamentoMapper;
import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoPagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.IntencaoPagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.Pagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.SessaoPagamento;
import cv.ntinnovation.vintipay.application.core.domain.enums.*;
import cv.ntinnovation.vintipay.application.core.domain.exception.PagamentoNaoEncontradoException;
import cv.ntinnovation.vintipay.application.core.domain.exception.SessaoPagamentoInvalidaException;
import cv.ntinnovation.vintipay.application.core.domain.rules.FingerPrintService;
import cv.ntinnovation.vintipay.application.port.in.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class WebhookPagamentoController {

    private final AtualizaStatusPagamentoUseCase atualizaStatusPagamentoUseCase;
    private final BuscaConfiguracaoPagamentoUseCase buscaConfiguracaoPagamentoUseCase;
    private final BuscaConfiguracaoPagamentoAtivaUseCase buscaConfiguracaoPagamentoAtivaUseCase;
    private final BuscaPagamentoUseCase buscaPagamentoUseCase;
    private final ValidaRetornoUseCase validaRetornoUseCase;
    private final WebhookPagamentoMapper webhookPagamentoMapper;
    private final FingerPrintService fingerPrintService;
    private final ProcessaIntegracaoUseCase<IntegracaoEnum> processaIntegracaoUseCase;
    private final NotificacaoUseCase notificacaoUseCase;
    private final WebhookUseCase webhookUseCase;
    private final SessaoPagamentoUseCase sessaoPagamentoUseCase;

    private final ValidaSessaoPagamentoUseCase validaSessaoPagamentoUseCase;

    @PostMapping(value = "/sisp/pagamentos/retorno/{reference}",
            consumes={"application/x-www-form-urlencoded;charset=UTF-8"}, produces={ MediaType.ALL_VALUE })
    public String handlePaymentWebhook(@PathVariable("reference") String reference,
                                       @RequestParam("fingerPrintCode") String authorization,
                                       @ModelAttribute SispWebhookRequest request,
                                       Model model, HttpServletRequest request2) {
        log.info("Recebido webhook do SISP - Reference: {}, Request: {}", reference, request);

        var pagamentoExistente = buscaPagamentoUseCase.buscarPorReferencia(reference);

        if (pagamentoExistente.isPresent()) {
            var retornoFingerprint = webhookPagamentoMapper.toRetornoFingerprint(request);

            retornoFingerprint.setPagamento(pagamentoExistente.get());

            retornoFingerprint.setAuthorizationCode(authorization);

            ConfiguracaoPagamento configuracaoPagamento = buscaConfiguracaoPagamentoAtivaUseCase.buscarPorClientId(pagamentoExistente.map(Pagamento::getIntencaoPagamento).orElse(IntencaoPagamento.builder().build()).getClientKey());
            log.info("PosAutCode {} :",configuracaoPagamento.getPosAutCode());
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
                    request.getMerchantRespPan(),
                    request.getMerchantRespCP(),
                    request.getMerchantRespTid()
            );
            log.debug("Pagamento atualizado: {}", pagamento);

            log.debug("Buscando configuração de pagamento");

            log.debug("Configuração encontrada: {}", configuracaoPagamento);

            CheckoutResponseDto responseDto = CheckoutResponseDto.builder()
                    .reference(pagamento.getReference())
                    .status(pagamento.getStatus().getStatus())
                    .message(mensagem)
                    .amount(pagamento.getIntencaoPagamento().getValor())
                    .currency(pagamento.getIntencaoPagamento().getMoeda())
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

            processaIntegracaoUseCase.processar(configuracaoPagamento,pagamento);
            
            log.debug("HMAC Fingerprint gerado: {}", hmacFingerprint);
            boolean noReturnUrl = configuracaoPagamento.getReturnUrl() == null || configuracaoPagamento.getReturnUrl().isEmpty();
            boolean returnAtivo = configuracaoPagamento.getReturnAtivo();

            webhookUseCase.enviarPagamentoWebhook(configuracaoPagamento.getWebhookUrl(), pagamento);

            validaSessaoPagamentoUseCase.invalidarSessao(responseDto.getMerchantSession());

            if(noReturnUrl || !returnAtivo ) {
                if(pagamento.getStatus().equals(StatusPagamentoEnum.PAGO)) {
                    // Construir URL para visualização do recibo
                    String urlVisualizarRecibo = request2.getScheme() + "://" + 
                                                request2.getServerName() + 
                                                (request2.getServerPort() != 80 && request2.getServerPort() != 443 ? ":" + request2.getServerPort() : "") + 
                                                "/recibo/view/" + pagamento.getReference();
                                                
                    // Enviar email para o cliente que realizou o pagamento
                    notificacaoUseCase.enviarEmailPagamento(
                        pagamento.getIntencaoPagamento().getContaPagamento().getEmail(),
                        pagamento.getIntencaoPagamento().getContaPagamento().getName(),
                        pagamento.getReference(), 
                        pagamento.getIntencaoPagamento().getValor().doubleValue(),
                        pagamento.getIntencaoPagamento().getDescricao(),
                        urlVisualizarRecibo
                    );
                    
                    // Enviar notificação para o comerciante/lojista
                    notificacaoUseCase.notificarComerciante(
                        configuracaoPagamento.getEmail(),  // Email do lojista
                        configuracaoPagamento.getNomeLoja(),  // Nome da loja
                        pagamento.getIntencaoPagamento().getContaPagamento().getName(),  // Nome do cliente
                        pagamento.getIntencaoPagamento().getContaPagamento().getEmail(),  // Email do cliente
                        pagamento.getReference(),  // Referência do pagamento
                        pagamento.getIntencaoPagamento().getValor().doubleValue()  // Valor do pagamento
                    );
                }
                model.addAttribute("pagamento", pagamento);
                model.addAttribute("erro", mensagem);
                return pagamento.getStatus().getRetorno();
            }else {
                model.addAttribute("response", responseDto);
                model.addAttribute("submitFormUrl", configuracaoPagamento.getReturnUrl());
                model.addAttribute("hmacFingerprint", hmacFingerprint);
                log.info("Redirecionando para: {}", configuracaoPagamento.getReturnUrl());
                request2.getSession().removeAttribute("PAYMENT_SESSION_ID");
                return "checkout-response";
            }

        } else {
            throw new PagamentoNaoEncontradoException("Pagamento não encontrado");
        }

    }


    private String getMensagemRetorno(MensagemRetornoEnum mensagemRetornoEnum, SispWebhookRequest request) {
        if (mensagemRetornoEnum.getMessageType().equals(MessageRespTypeEnum.FALHA)){
            int codigoErro;
            try {
                codigoErro = Integer.parseInt(request.getMerchantRespErrorCode());
            }catch (NumberFormatException e){
                codigoErro = 0;
            }
            return MessageErrorEnum.fromCodigo(codigoErro).getMensagem();
        }
        return mensagemRetornoEnum.getDescricao();
    }

    @PostMapping(value = "/sisp/estorno/retorno/{reference}",
            consumes={"application/x-www-form-urlencoded;charset=UTF-8"}, produces={ MediaType.ALL_VALUE })
    public RedirectView estornarPagamentoRetorno(@PathVariable("reference") String reference,
                                                 @RequestParam("fingerPrintCode") String authorization,
                                                 @ModelAttribute SispWebhookRequest request,
                                                 RedirectAttributes attributes ) {

        log.info("Recebido estorno do SISP - Reference: {}, Request: {} ", reference, request );
        var pagamento = buscaPagamentoUseCase.buscarPorReferencia(reference)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado: " + reference));

        var retornoFingerprint = webhookPagamentoMapper.toRetornoFingerprint(request);

        retornoFingerprint.setPagamento(pagamento);

        retornoFingerprint.setAuthorizationCode(authorization);

        ConfiguracaoPagamento configuracaoPagamento = buscaConfiguracaoPagamentoUseCase.buscarPorPosId(pagamento.getIntencaoPagamento().getPosId());
        retornoFingerprint.setPosAutCode(configuracaoPagamento.getPosAutCode());

        validaRetornoUseCase.executar(retornoFingerprint);

        log.debug("Processando mensagem de retorno");
        MensagemRetornoEnum mensagemRetornoEnum = MensagemRetornoEnum.getMensagemRetornoEnum(
                request.getMessageType(),
                request.getMerchantResp(),
                request.getUserCancelled());

        String mensagem = mensagemRetornoEnum.getDescricao();
        log.debug("Mensagem de retorno: {}", mensagem);

        pagamento = atualizaStatusPagamentoUseCase.atualizar(
                reference,
                mensagemRetornoEnum.getStatus(),
                mensagem,
                request.getResultFingerPrint(),
                null,
                request.getMerchantRespCP(),
                request.getMerchantRespTid()
        );

        return new RedirectView("/user/pagamentos/"+reference);

    }
}
