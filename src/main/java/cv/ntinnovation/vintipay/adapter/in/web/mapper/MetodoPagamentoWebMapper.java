package cv.ntinnovation.vintipay.adapter.in.web.mapper;

import cv.ntinnovation.vintipay.adapter.in.web.dto.MetodoPagamentoRequest;
import cv.ntinnovation.vintipay.adapter.in.web.dto.MetodoPagamentoResponse;
import cv.ntinnovation.vintipay.application.core.domain.entity.MetodoPagamento;
import org.springframework.stereotype.Component;

@Component
public class MetodoPagamentoWebMapper {

    public MetodoPagamento toMetodoPagamento(MetodoPagamentoRequest request) {
        MetodoPagamento metodoPagamento = new MetodoPagamento();
        metodoPagamento.setName(request.getName());
        return metodoPagamento;
    }

    public MetodoPagamentoResponse toResponse(MetodoPagamento metodoPagamento) {
        return MetodoPagamentoResponse.builder()
                .id(metodoPagamento.getId())
                .name(metodoPagamento.getName())
                .build();
    }
}
