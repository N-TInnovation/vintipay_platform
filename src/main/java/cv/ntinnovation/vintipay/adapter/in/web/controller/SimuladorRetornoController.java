package cv.ntinnovation.vintipay.adapter.in.web.controller;

import cv.ntinnovation.vintipay.adapter.in.web.dto.SispCheckoutRequest;
import cv.ntinnovation.vintipay.adapter.in.web.dto.SispWebhookRequest;
import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoPagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.DataFingerPrint;
import cv.ntinnovation.vintipay.application.core.domain.enums.AmbienteEnum;
import cv.ntinnovation.vintipay.application.core.domain.enums.MessageRespTypeEnum;
import cv.ntinnovation.vintipay.application.core.domain.enums.MoedaEnum;
import cv.ntinnovation.vintipay.application.core.domain.exception.ConfiguracaoPagamentoIvalidaException;
import cv.ntinnovation.vintipay.application.core.domain.exception.FingerprintInvalidoException;
import cv.ntinnovation.vintipay.application.core.domain.rules.DataFingerPrint810MPResponse;
import cv.ntinnovation.vintipay.application.core.domain.rules.FingerPrintService;
import cv.ntinnovation.vintipay.application.core.utils.HashGenerate;
import cv.ntinnovation.vintipay.application.port.in.BuscaConfiguracaoPagamentoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@Controller
@RequestMapping("/simulador/")
@RequiredArgsConstructor
public class SimuladorRetornoController {
    private final BuscaConfiguracaoPagamentoUseCase buscaConfiguracaoPagamentoUseCase;
    private final FingerPrintService fingerPrintService;

    @PostMapping(value = "sisp/3ds_middleware_php/public/3ds_init.php")
    public String index(
            @RequestParam(value = "FingerPrint") String fingerPrint,
            @RequestParam(value = "TimeStamp") String timeStamp,
            @RequestParam(value = "FingerPrintVersion") String fingerPrintVersion,
            @ModelAttribute SispCheckoutRequest sispCheckoutRequest,
            Model model
            ) {
        if(!sispCheckoutRequest.getFingerprint().equals(fingerPrint)) {
            throw new FingerprintInvalidoException("FingerPrint does not match");
        }
        if(!sispCheckoutRequest.getTimeStamp().equals(timeStamp)) {
            throw new FingerprintInvalidoException("TimeStamp does not match");
        }
        if (!sispCheckoutRequest.getFingerprintversion().equals(fingerPrintVersion)) {
            throw new FingerprintInvalidoException("FingerPrintVersion does not match");
        }

        var configuracaoPagamento = buscaConfiguracaoPagamentoUseCase.buscarPorPosId(sispCheckoutRequest.getPosID());

        validaConfiguracaoPagamento(configuracaoPagamento);

        validaFingerPrint(sispCheckoutRequest, configuracaoPagamento);

        model.addAttribute("request", sispCheckoutRequest);

        return "simulador/sisp/3ds_middleware_php";
    }

    @PostMapping(value = "sisp/3ds_middleware_php/public/retorno.php")
    public String retorno(@ModelAttribute SispCheckoutRequest sispCheckoutRequest, Model model) {
        var configuracaoPagamento = buscaConfiguracaoPagamentoUseCase.buscarPorPosId(sispCheckoutRequest.getPosID());

        validaConfiguracaoPagamento(configuracaoPagamento);

        validaFingerPrint(sispCheckoutRequest, configuracaoPagamento);

        SispWebhookRequest request = responseSucesso(sispCheckoutRequest, configuracaoPagamento);
        model.addAttribute("request", request);
        model.addAttribute("retornoUrl", sispCheckoutRequest.getUrlMerchantResponse());
        return "simulador/sisp/3ds_retorno_php";
    }

    private SispWebhookRequest responseSucesso(SispCheckoutRequest sispCheckoutRequest, ConfiguracaoPagamento configuracaoPagamento) {
        SispWebhookRequest request = new SispWebhookRequest();
        request.setMerchantRespMerchantRef(sispCheckoutRequest.getMerchantRef());
        request.setMerchantRespMerchantSession(sispCheckoutRequest.getMerchantSession());
        request.setMerchantRespErrorCode("");
        request.setMerchantRespErrorDescription("");
        request.setMerchantRespErrorDetail("");
        request.setMerchantRespAdditionalErrorMessage("");
        request.setMerchantRespCP("987");
        request.setMerchantRespTid("26");

        String originalCard = sispCheckoutRequest.getCardNumber();
        int visibleDigits = 4;

        String lastFour = originalCard.substring(originalCard.length() - visibleDigits);
        String maskedCard = "*".repeat(originalCard.length() - visibleDigits) + lastFour;
        request.setMerchantRespPan(maskedCard);

        request.setMerchantRespPurchaseAmount(new BigDecimal(sispCheckoutRequest.getAmount()));
        request.setMerchantRespMessageID("J38HvwHPPn22P1se0OxJ");
        request.setMerchantResp("C");
        request.setLanguageMessages(sispCheckoutRequest.getLanguageMessages());
        request.setMessageType(MessageRespTypeEnum.SUCESSO_COMPRAS);
        request.setMerchantRespTimeStamp(sispCheckoutRequest.getTimeStamp());
        request.setResultFingerPrintVersion(sispCheckoutRequest.getFingerprintversion());
        request.setMerchantRespEntityCode(sispCheckoutRequest.getEntityCode());
        request.setMerchantRespReloadCode("");
        request.setMerchantRespReferenceNumber(sispCheckoutRequest.getReferenceNumber());


        DataFingerPrint810MPResponse dataFingerPrint810MPResponse = DataFingerPrint810MPResponse.builder().build();
        dataFingerPrint810MPResponse.setMessageType(request.getMessageType().getCode());
        dataFingerPrint810MPResponse.setMerchantRespCP(request.getMerchantRespCP());
        dataFingerPrint810MPResponse.setMerchantRespTid(request.getMerchantRespTid());
        dataFingerPrint810MPResponse.setMerchantRespMerchantRef(request.getMerchantRespMerchantRef());
        dataFingerPrint810MPResponse.setMerchantRespMerchantSession(request.getMerchantRespMerchantSession());
        dataFingerPrint810MPResponse.setMerchantRespPurchaseAmount(request.getMerchantRespPurchaseAmount().toString());
        dataFingerPrint810MPResponse.setMerchantRespMessageID(request.getMerchantRespMessageID());
        dataFingerPrint810MPResponse.setMerchantRespPan(request.getMerchantRespPan());
        dataFingerPrint810MPResponse.setMerchantResp(request.getMerchantResp());
        dataFingerPrint810MPResponse.setMerchantRespTimeStamp(request.getMerchantRespTimeStamp());
        dataFingerPrint810MPResponse.setMerchantRespEntityCode(request.getMerchantRespEntityCode());
        dataFingerPrint810MPResponse.setMerchantRespReferenceNumber(request.getMerchantRespReferenceNumber());
        dataFingerPrint810MPResponse.setMerchantRespClientReceipt(request.getMerchantRespClientReceipt());
        dataFingerPrint810MPResponse.setMerchantRespAdditionalErrorMessage(request.getMerchantRespAdditionalErrorMessage());
        dataFingerPrint810MPResponse.setMerchantRespReloadCode(request.getMerchantRespReloadCode());
        dataFingerPrint810MPResponse.setResultFingerPrint(request.getResultFingerPrint());
        dataFingerPrint810MPResponse.setPosAutCode(configuracaoPagamento.getPosAutCode());
        var fingerPrintResult = HashGenerate.HashGenerate810MP(dataFingerPrint810MPResponse);

        request.setResultFingerPrint(fingerPrintResult);

        return request;
    }

    private void validaConfiguracaoPagamento(ConfiguracaoPagamento configuracaoPagamento) {
        if(configuracaoPagamento.getAtivo() == null || !configuracaoPagamento.getAtivo()) {
            throw new ConfiguracaoPagamentoIvalidaException("Configuração de pagamento inativa");
        }

        if(configuracaoPagamento.getAmbiente() == null || !configuracaoPagamento.getAmbiente().equals(AmbienteEnum.DESENVOLVIMENTO)) {
            throw new ConfiguracaoPagamentoIvalidaException("Configuração de ambiente inválida");
        }
    }

    private void validaFingerPrint(SispCheckoutRequest sispCheckoutRequest, ConfiguracaoPagamento configuracaoPagamento) {
        // Gerar fingerprint usando o FingerPrintService
        DataFingerPrint dataFingerPrint = DataFingerPrint.builder()
                .posAutCode(configuracaoPagamento.getPosAutCode())
                .timestamp(sispCheckoutRequest.getTimeStamp())
                .amount(new BigDecimal(sispCheckoutRequest.getAmount()))
                .merchantRef(sispCheckoutRequest.getMerchantRef())
                .merchantSession(sispCheckoutRequest.getMerchantSession())
                .posID(sispCheckoutRequest.getPosID())
                .currency(sispCheckoutRequest.getCurrency())
                .transactionCode(sispCheckoutRequest.getTransactionCode())
                .entityCode(sispCheckoutRequest.getEntityCode() == null ? "" : sispCheckoutRequest.getEntityCode())
                .referenceNumber(sispCheckoutRequest.getReferenceNumber() == null ? "" : sispCheckoutRequest.getReferenceNumber())
                .build();

        var fingerPrintResult = fingerPrintService.generateFingerPrint(dataFingerPrint);

        if(!fingerPrintResult.getHashFingerPrint().equals(sispCheckoutRequest.getFingerprint())) {
            throw new FingerprintInvalidoException("FingerPrint does not match");
        }
    }

}
