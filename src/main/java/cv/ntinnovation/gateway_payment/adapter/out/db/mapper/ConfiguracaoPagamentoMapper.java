package cv.ntinnovation.gateway_payment.adapter.out.db.mapper;

import cv.ntinnovation.gateway_payment.adapter.out.db.entity.ConfiguracaoPagamentoEntity;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.ConfiguracaoPagamento;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class ConfiguracaoPagamentoMapper {

    public ConfiguracaoPagamentoEntity toEntity(ConfiguracaoPagamento domain) {
        if (domain == null) {
            return null;
        }

        return ConfiguracaoPagamentoEntity.builder()
                .id(domain.getId())
                .posId(domain.getPosId())
                .merchantId(domain.getMerchantId())
                .terminalId(domain.getTerminalId())
                .apiKey(domain.getApiKey())
                .apiSecret(domain.getApiSecret())
                .posAutCode(domain.getPosAutCode())
                .returnUrl(domain.getReturnUrl())
                .isActive(domain.getIsActive())
                .returnAutCode(domain.getReturnAutCode())
                .nomeLoja(domain.getNomeLoja())
                .urlLoja(domain.getUrlLoja())
                .telefone(domain.getTelefone())
                .email(domain.getEmail())
                .createdAt(domain.getCreatedAt() != null ? domain.getCreatedAt() : LocalDateTime.now())
                .updatedAt(domain.getUpdatedAt() != null ? domain.getUpdatedAt() : LocalDateTime.now())
                .build();
    }

    public ConfiguracaoPagamento toDomain(ConfiguracaoPagamentoEntity entity) {
        if (entity == null) {
            return null;
        }

        return ConfiguracaoPagamento.builder()
                .id(entity.getId())
                .posId(entity.getPosId())
                .merchantId(entity.getMerchantId())
                .terminalId(entity.getTerminalId())
                .apiKey(entity.getApiKey())
                .apiSecret(entity.getApiSecret())
                .returnAutCode(entity.getReturnAutCode())
                .posAutCode(entity.getPosAutCode())
                .returnUrl(entity.getReturnUrl())
                .isActive(entity.getIsActive())
                .nomeLoja(entity.getNomeLoja())
                .urlLoja(entity.getUrlLoja())
                .telefone(entity.getTelefone())
                .email(entity.getEmail())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
