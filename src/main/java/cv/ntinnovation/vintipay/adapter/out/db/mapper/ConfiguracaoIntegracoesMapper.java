package cv.ntinnovation.vintipay.adapter.out.db.mapper;

import cv.ntinnovation.vintipay.adapter.out.db.entity.ConfiguracaoIntegracoesEntity;
import cv.ntinnovation.vintipay.adapter.out.db.entity.ConfiguracaoPagamentoEntity;
import cv.ntinnovation.vintipay.adapter.out.db.entity.UsuarioEntity;
import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoIntegracoes;
import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoPagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import cv.ntinnovation.vintipay.application.core.domain.enums.IntegracaoEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConfiguracaoIntegracoesMapper {

    private final UsuarioMapper usuarioMapper;

    public ConfiguracaoIntegracoes toDomain(ConfiguracaoIntegracoesEntity entity) {
        if (entity == null) {
            return null;
        }
        
        ConfiguracaoIntegracoes.ConfiguracaoIntegracoesBuilder builder = ConfiguracaoIntegracoes.builder()
            .id(entity.getId())
            .isActive(entity.getIsActive())
            .integracaoEnum(IntegracaoEnum.getByName(entity.getName()))
            .description(entity.getDescription())
            .version(entity.getVersion())
            .baseUrl(entity.getBaseUrl())
            .apiKey(entity.getApiKey())
            .apiSecret(entity.getApiSecret())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt());
        
        // Mapear o usuário
        if (entity.getUsuario() != null) {
            builder.usuario(usuarioMapper.toDomain(entity.getUsuario()));
        }
        
        // Para o relacionamento com ConfiguracaoPagamento, apenas mapear o ID
        // para evitar recursão infinita
        if (entity.getConfiguracaoPagamento() != null) {
            ConfiguracaoPagamento config = new ConfiguracaoPagamento();
            config.setId(entity.getConfiguracaoPagamento().getId());
            builder.configuracaoPagamento(config);
            log.debug("Mapeado ID da configuração de pagamento: {}", entity.getConfiguracaoPagamento().getId());
        }
        
        return builder.build();
    }

    public ConfiguracaoIntegracoesEntity toEntity(ConfiguracaoIntegracoes domain) {
        if (domain == null) {
            return null;
        }
        
        ConfiguracaoIntegracoesEntity.ConfiguracaoIntegracoesEntityBuilder builder = ConfiguracaoIntegracoesEntity.builder()
            .id(domain.getId())
            .isActive(domain.isActive())
            .name(domain.getIntegracaoEnum() != null ? domain.getIntegracaoEnum().getName() : null)
            .description(domain.getDescription())
            .version(domain.getVersion())
            .baseUrl(domain.getBaseUrl())
            .apiKey(domain.getApiKey())
            .apiSecret(domain.getApiSecret())
            .createdAt(domain.getCreatedAt())
            .updatedAt(domain.getUpdatedAt());
            
        // Mapear o usuário
        if (domain.getUsuario() != null) {
            UsuarioEntity usuarioEntity = usuarioMapper.toEntity(domain.getUsuario());
            builder.usuario(usuarioEntity);
            log.debug("Mapeado usuário ID: {}", usuarioEntity.getId());
        }
        
        // Para o relacionamento com ConfiguracaoPagamento, apenas mapear o ID
        // para evitar recursão infinita
        if (domain.getConfiguracaoPagamento() != null && domain.getConfiguracaoPagamento().getId() != null) {
            ConfiguracaoPagamentoEntity entity = new ConfiguracaoPagamentoEntity();
            entity.setId(domain.getConfiguracaoPagamento().getId());
            builder.configuracaoPagamento(entity);
            log.debug("Mapeado ID da configuração de pagamento: {}", domain.getConfiguracaoPagamento().getId());
        }
        
        return builder.build();
    }
}
