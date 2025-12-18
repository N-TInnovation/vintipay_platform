package cv.ntinnovation.vintipay.adapter.in.web.mapper;

import cv.ntinnovation.vintipay.adapter.in.web.dto.SispWebhookRequest;
import cv.ntinnovation.vintipay.application.core.domain.enums.MessageRespTypeEnum;
import cv.ntinnovation.vintipay.application.core.domain.rules.RetornoFingerprint;
import org.springframework.stereotype.Component;

@Component
public class WebhookPagamentoMapper {

    public RetornoFingerprint toRetornoFingerprint( SispWebhookRequest sispWebhookRequest) {
        var messageType = sispWebhookRequest.getMessageType();
        if(messageType == null){
            messageType = MessageRespTypeEnum.FALHA_OUTROS;
        }
        messageType.getRetornoFingerprint().toBuilder(sispWebhookRequest);
        return messageType.getRetornoFingerprint();
    }
}
