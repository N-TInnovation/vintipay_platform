package cv.ntinnovation.vintipay.adapter.out.persistence.mapper;

import cv.ntinnovation.vintipay.adapter.out.persistence.entity.MetodoPagamentoEntity;
import cv.ntinnovation.vintipay.application.core.domain.entity.MetodoPagamento;
import org.springframework.stereotype.Component;

@Component
public class MetodoPagamentoEntityMapper {

    public MetodoPagamento toDomain(MetodoPagamentoEntity entity) {
        if (entity == null) {
            return null;
        }
        return MetodoPagamento.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    public MetodoPagamentoEntity toEntity(MetodoPagamento domain) {
        if (domain == null) {
            return null;
        }
        MetodoPagamentoEntity entity = new MetodoPagamentoEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        return entity;
    }
}
