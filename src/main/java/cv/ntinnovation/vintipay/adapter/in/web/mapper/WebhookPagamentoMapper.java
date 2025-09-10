package cv.ntinnovation.vintipay.adapter.in.web.mapper;

import cv.ntinnovation.vintipay.adapter.in.web.dto.SispWebhookRequest;
import cv.ntinnovation.vintipay.application.core.domain.enums.MessageRespTypeEnum;
import cv.ntinnovation.vintipay.application.core.domain.rules.RetornoFingerprint;
import org.springframework.stereotype.Component;

@Component
public class WebhookPagamentoMapper {

    public RetornoFingerprint toRetornoFingerprint( SispWebhookRequest sispWebhookRequest) {
        var messageType = sispWebhookRequest.getMessageType();

        if(messageType != null) {
            messageType.getRetornoFingerprint().toBuilder(sispWebhookRequest);
            return messageType.getRetornoFingerprint();
        }else {
           MessageRespTypeEnum.FALHA_OUTROS.getRetornoFingerprint().toBuilder(sispWebhookRequest);
           return MessageRespTypeEnum.FALHA_OUTROS.getRetornoFingerprint();
        }

    }
}
