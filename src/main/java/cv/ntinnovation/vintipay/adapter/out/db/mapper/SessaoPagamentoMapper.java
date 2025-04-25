package cv.ntinnovation.vintipay.adapter.out.db.mapper;

import cv.ntinnovation.vintipay.adapter.out.db.entity.SessaoPagamentoEntity;
import cv.ntinnovation.vintipay.application.core.domain.entity.SessaoPagamento;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class SessaoPagamentoMapper {

    public SessaoPagamentoEntity toEntity(SessaoPagamento domain) {
        if (domain == null) {
            return null;
        }

        return SessaoPagamentoEntity.builder()
                .id(Long.parseLong(domain.getId()))
                .sessionId(domain.getId())
                .reference(domain.getReferencia())
                .clientId(domain.getClientId())
                .ipAddress(domain.getIpAddress())
                .userAgent(domain.getUserAgent())
                .createdAt(domain.getDataCriacao() != null ? domain.getDataCriacao() : LocalDateTime.now())
                .expiresAt(domain.getDataExpiracao())
                .valid(domain.getValid())
                .build();
    }

    public SessaoPagamento toDomain(SessaoPagamentoEntity entity) {
        if (entity == null) {
            return null;
        }

        return SessaoPagamento.builder()
                .id(String.valueOf(entity.getId()))
                .id(entity.getSessionId())
                .referencia(entity.getReference())
                .clientId(entity.getClientId())
                .ipAddress(entity.getIpAddress())
                .userAgent(entity.getUserAgent())
                .dataCriacao(entity.getCreatedAt())
                .dataExpiracao(entity.getExpiresAt())
                .valid(entity.isValid())
                .build();
    }
}
