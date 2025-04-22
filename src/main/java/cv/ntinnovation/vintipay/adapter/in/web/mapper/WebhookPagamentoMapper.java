package cv.ntinnovation.vintipay.adapter.in.web.mapper;

import cv.ntinnovation.vintipay.adapter.in.web.dto.SispWebhookRequest;
import cv.ntinnovation.vintipay.application.core.domain.enums.MessageRespTypeEnum;
import cv.ntinnovation.vintipay.application.core.domain.rules.DataFingerPrint6Response;
import cv.ntinnovation.vintipay.application.core.domain.rules.DataFingerPrint810MPResponse;
import cv.ntinnovation.vintipay.application.core.domain.rules.DataFingerPrintOutroResponse;
import cv.ntinnovation.vintipay.application.core.domain.rules.RetornoFingerprint;
import org.springframework.stereotype.Component;

@Component
public class WebhookPagamentoMapper {

    public RetornoFingerprint toRetornoFingerprint( SispWebhookRequest sispWebhookRequest) {
        var messageType = sispWebhookRequest.getMessageType();
        if(messageType != null && messageType.getRetornoFingerprint() instanceof DataFingerPrint810MPResponse) {
            var dataFingerPrint810MPResponse = (DataFingerPrint810MPResponse) messageType.getRetornoFingerprint();
            dataFingerPrint810MPResponse.setMessageType(messageType.getCode());
            dataFingerPrint810MPResponse.setMerchantRespCP(sispWebhookRequest.getMerchantRespCP());
            dataFingerPrint810MPResponse.setMerchantRespTid(sispWebhookRequest.getMerchantRespTid());
            dataFingerPrint810MPResponse.setMerchantRespMerchantRef(sispWebhookRequest.getMerchantRespMerchantRef());
            dataFingerPrint810MPResponse.setMerchantRespMerchantSession(sispWebhookRequest.getMerchantRespMerchantSession());
            dataFingerPrint810MPResponse.setMerchantRespPurchaseAmount(sispWebhookRequest.getMerchantRespPurchaseAmount().toString());
            dataFingerPrint810MPResponse.setMerchantRespMessageID(sispWebhookRequest.getMerchantRespMessageID());
            dataFingerPrint810MPResponse.setMerchantRespPan(sispWebhookRequest.getMerchantRespPan());
            dataFingerPrint810MPResponse.setMerchantResp(sispWebhookRequest.getMerchantResp());
            dataFingerPrint810MPResponse.setMerchantRespTimeStamp(sispWebhookRequest.getMerchantRespTimeStamp());
            dataFingerPrint810MPResponse.setMerchantRespEntityCode(sispWebhookRequest.getMerchantRespEntityCode());
            dataFingerPrint810MPResponse.setMerchantRespReferenceNumber(sispWebhookRequest.getMerchantRespReferenceNumber());
            dataFingerPrint810MPResponse.setMerchantRespClientReceipt(sispWebhookRequest.getMerchantRespClientReceipt());
            dataFingerPrint810MPResponse.setMerchantRespAdditionalErrorMessage(sispWebhookRequest.getMerchantRespAdditionalErrorMessage());
            dataFingerPrint810MPResponse.setMerchantRespReloadCode(sispWebhookRequest.getMerchantRespReloadCode());
            dataFingerPrint810MPResponse.setResultFingerPrint(sispWebhookRequest.getResultFingerPrint());

            return dataFingerPrint810MPResponse;
        }if ( messageType != null && messageType.getRetornoFingerprint() instanceof DataFingerPrint6Response) {
            var dataFingerPrint6Response = (DataFingerPrint6Response) messageType.getRetornoFingerprint();
            dataFingerPrint6Response.setMessageType(messageType.getCode());
            dataFingerPrint6Response.setMerchantRespErrorCode(sispWebhookRequest.getMerchantRespErrorCode());
            dataFingerPrint6Response.setMerchantRespErrorDetail(sispWebhookRequest.getMerchantRespErrorDetail());
            dataFingerPrint6Response.setMerchantRespErrorDescription(sispWebhookRequest.getMerchantRespErrorDescription());
            dataFingerPrint6Response.setMerchantRespMerchantRef(sispWebhookRequest.getMerchantRespMerchantRef());
            dataFingerPrint6Response.setMerchantRespMerchantSession(sispWebhookRequest.getMerchantRespMerchantSession());
            dataFingerPrint6Response.setMerchantRespAdditionalErrorMessage(sispWebhookRequest.getMerchantRespAdditionalErrorMessage());
            dataFingerPrint6Response.setResultFingerPrint(sispWebhookRequest.getResultFingerPrint());
            dataFingerPrint6Response.setMerchantRespMessageID(sispWebhookRequest.getMerchantRespMessageID());
            dataFingerPrint6Response.setMerchantRespTimeStamp(sispWebhookRequest.getMerchantRespTimeStamp());

            return dataFingerPrint6Response;
        }else {
            var dataFingerPrintOutroResponse = (DataFingerPrintOutroResponse) MessageRespTypeEnum.FALHA_OUTROS.getRetornoFingerprint();
            dataFingerPrintOutroResponse.setMessageType(MessageRespTypeEnum.FALHA_OUTROS.getCode());
            dataFingerPrintOutroResponse.setMerchantRespMerchantRef(sispWebhookRequest.getMerchantRespMerchantRef());
            dataFingerPrintOutroResponse.setMerchantRespMerchantSession(sispWebhookRequest.getMerchantRespMerchantSession());
            dataFingerPrintOutroResponse.setResultFingerPrint(sispWebhookRequest.getResultFingerPrint());

            return dataFingerPrintOutroResponse;
        }

    }
}
