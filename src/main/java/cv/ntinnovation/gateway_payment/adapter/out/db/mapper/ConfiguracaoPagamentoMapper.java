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
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public ConfiguracaoPagamentoEntity toEntity(ConfiguracaoPagamento configuracao) {
        ConfiguracaoPagamentoEntity entity = new ConfiguracaoPagamentoEntity();
        entity.setId(configuracao.getId());
        entity.setPosId(configuracao.getPosId());
        entity.setMerchantId(configuracao.getMerchantId());
        entity.setTerminalId(configuracao.getTerminalId());
        entity.setApiKey(configuracao.getApiKey());
        entity.setApiSecret(configuracao.getApiSecret());
        entity.setIsActive(configuracao.getIsActive());
        entity.setCreatedAt(configuracao.getCreatedAt());
        entity.setUpdatedAt(configuracao.getUpdatedAt());
        return entity;
    }
}
