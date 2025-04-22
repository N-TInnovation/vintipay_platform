package cv.ntinnovation.vintipay.adapter.out.db.mapper;

import cv.ntinnovation.vintipay.application.core.domain.enums.MessageRespTypeEnum;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class MessageRespTypeEnumConverter implements Converter<String, MessageRespTypeEnum> {

    @Override
    public MessageRespTypeEnum convert(String source) {
        return MessageRespTypeEnum.fromCode(source);
    }
}
