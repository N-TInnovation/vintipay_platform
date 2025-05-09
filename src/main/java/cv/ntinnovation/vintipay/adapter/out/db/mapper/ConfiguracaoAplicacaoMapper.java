package cv.ntinnovation.vintipay.adapter.out.db.mapper;

import cv.ntinnovation.vintipay.adapter.out.db.entity.ConfiguracaoAplicacaoEntity;
import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoAplicacao;
import cv.ntinnovation.vintipay.application.core.domain.enums.IntegracaoEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConfiguracaoAplicacaoMapper {

    private final ConfiguracaoPagamentoMapper configuracaoPagamentoMapper;

    public ConfiguracaoAplicacao toDomain(ConfiguracaoAplicacaoEntity entity) {
        return ConfiguracaoAplicacao.builder()
            .id(entity.getId())
            .isActive(entity.getIsActive())
            .integracaoEnum(IntegracaoEnum.getByName(entity.getName()))
            .description(entity.getDescription())
            .version(entity.getVersion())
            .baseUrl(entity.getBaseUrl())
            .apiKey(entity.getApiKey())
            .apiSecret(entity.getApiSecret())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .configuracaoPagamento(configuracaoPagamentoMapper.toDomain(entity.getConfiguracaoPagamento()))
            .build();
    }

    public ConfiguracaoAplicacaoEntity toEntity(ConfiguracaoAplicacao domain) {
        return ConfiguracaoAplicacaoEntity.builder()
            .id(domain.getId())
            .isActive(domain.isActive())
            .name(domain.getIntegracaoEnum().getName())
            .description(domain.getDescription())
            .version(domain.getVersion())
            .baseUrl(domain.getBaseUrl())
            .apiKey(domain.getApiKey())
            .apiSecret(domain.getApiSecret())
            .createdAt(domain.getCreatedAt())
            .updatedAt(domain.getUpdatedAt())
            .configuracaoPagamento(configuracaoPagamentoMapper.toEntity(domain.getConfiguracaoPagamento()))
            .build();
    }
}
