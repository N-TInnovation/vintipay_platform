package cv.ntinnovation.vintipay.adapter.out.persistence.converter;

import cv.ntinnovation.vintipay.application.core.domain.enums.IntencaoPagamentoEnum;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class IntencaoPagamentoConverter implements AttributeConverter<IntencaoPagamentoEnum, String> {

    @Override
    public String convertToDatabaseColumn(IntencaoPagamentoEnum intencaoPagamentoEnum) {
        if (intencaoPagamentoEnum == null) {
            return null;
        }
        return intencaoPagamentoEnum.getIntencaoPagamento();
    }

    @Override
    public IntencaoPagamentoEnum convertToEntityAttribute(String intencaoPagamento) {
        if (intencaoPagamento == null || intencaoPagamento.trim().isEmpty()) {
            return null;
        }
        return IntencaoPagamentoEnum.findByIntencaoPagamento(intencaoPagamento);
    }
}
