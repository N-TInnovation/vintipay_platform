package cv.ntinnovation.gateway_payment.adapter.out.db.mapper;

import cv.ntinnovation.gateway_payment.adapter.out.db.entity.ConfiguracaoPagamentoEntity;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.ConfiguracaoPagamento;
import org.springframework.stereotype.Component;

@Component
public class ConfiguracaoPagamentoMapper {

    public ConfiguracaoPagamento toConfiguracao(ConfiguracaoPagamentoEntity entity) {
        return ConfiguracaoPagamento.builder()
                .id(entity.getId())
                .posId(entity.getPosId())
                .merchantId(entity.getMerchantId())
                .terminalId(entity.getTerminalId())
                .apiKey(entity.getApiKey())
                .apiSecret(entity.getApiSecret())
                .posAutCode(entity.getPosAutCode())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public ConfiguracaoPagamentoEntity toEntity(ConfiguracaoPagamento configuracao) {
        return ConfiguracaoPagamentoEntity.builder()
                .id(configuracao.getId())
                .posId(configuracao.getPosId())
                .merchantId(configuracao.getMerchantId())
                .terminalId(configuracao.getTerminalId())
                .apiKey(configuracao.getApiKey())
                .apiSecret(configuracao.getApiSecret())
                .posAutCode(configuracao.getPosAutCode())
                .isActive(configuracao.getIsActive())
                .createdAt(configuracao.getCreatedAt())
                .updatedAt(configuracao.getUpdatedAt())
                .build();
    }
}
