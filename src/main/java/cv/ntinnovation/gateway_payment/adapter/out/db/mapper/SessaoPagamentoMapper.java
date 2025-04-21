package cv.ntinnovation.gateway_payment.adapter.out.db.mapper;

import cv.ntinnovation.gateway_payment.adapter.out.db.entity.SessaoPagamentoEntity;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.SessaoPagamento;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class SessaoPagamentoMapper {

    public SessaoPagamentoEntity toEntity(SessaoPagamento domain) {
        if (domain == null) {
            return null;
        }

        return SessaoPagamentoEntity.builder()
                .id(domain.getId())
                .sessionId(domain.getSessionId())
                .reference(domain.getReference())
                .clientId(domain.getClientId())
                .ipAddress(domain.getIpAddress())
                .userAgent(domain.getUserAgent())
                .createdAt(domain.getCreatedAt() != null ? domain.getCreatedAt() : LocalDateTime.now())
                .expiresAt(domain.getExpiresAt())
                .valid(domain.isValid())
                .build();
    }

    public SessaoPagamento toDomain(SessaoPagamentoEntity entity) {
        if (entity == null) {
            return null;
        }

        return SessaoPagamento.builder()
                .id(entity.getId())
                .sessionId(entity.getSessionId())
                .reference(entity.getReference())
                .clientId(entity.getClientId())
                .ipAddress(entity.getIpAddress())
                .userAgent(entity.getUserAgent())
                .createdAt(entity.getCreatedAt())
                .expiresAt(entity.getExpiresAt())
                .valid(entity.isValid())
                .build();
    }
}
