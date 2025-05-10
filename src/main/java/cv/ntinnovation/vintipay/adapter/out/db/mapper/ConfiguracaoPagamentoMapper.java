package cv.ntinnovation.vintipay.adapter.out.db.mapper;

import cv.ntinnovation.vintipay.adapter.out.db.entity.ConfiguracaoIntegracoesEntity;
import cv.ntinnovation.vintipay.adapter.out.db.entity.ConfiguracaoPagamentoEntity;
import cv.ntinnovation.vintipay.adapter.out.db.entity.UsuarioEntity;
import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoIntegracoes;
import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoPagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import cv.ntinnovation.vintipay.application.core.domain.enums.AmbienteEnum;
import cv.ntinnovation.vintipay.application.core.domain.enums.IntegracaoEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConfiguracaoPagamentoMapper {

    private final UsuarioMapper usuarioMapper;

    public ConfiguracaoPagamentoEntity toEntity(ConfiguracaoPagamento domain) {
        if (domain == null) {
            return null;
        }

        ConfiguracaoPagamentoEntity.ConfiguracaoPagamentoEntityBuilder builder = ConfiguracaoPagamentoEntity.builder()
                .id(domain.getId())
                .posId(domain.getPosId())
                .merchantId(domain.getMerchantId() != null ? domain.getMerchantId() : "")
                .apiKey(domain.getApiKey())
                .apiSecret(domain.getApiSecret())
                .posAutCode(domain.getPosAutCode())
                .returnUrl(domain.getReturnUrl() != null ? domain.getReturnUrl() : "")
                .isActive(domain.getIsActive() != null ? domain.getIsActive() : true)
                .returnAutCode(domain.getReturnAutCode() != null ? domain.getReturnAutCode() : "")
                .nomeLoja(domain.getNomeLoja() != null ? domain.getNomeLoja() : "")
                .urlLoja(domain.getUrlLoja() != null ? domain.getUrlLoja() : "")
                .telefone(domain.getTelefone())
                .email(domain.getEmail())
                .ambiente(domain.getAmbiente().getAmbiente())
                .webhookUrl(domain.getWebhookUrl())
                .webhookSecret(domain.getWebhookSecret())
                .ativo(domain.getAtivo() != null ? domain.getAtivo() : true)
                .webhookAtivo(domain.getWebhookAtivo() != null ? domain.getWebhookAtivo() : false)
                .returnAtivo(domain.getReturnAtivo() != null ? domain.getReturnAtivo() : false)
                .observacoes(domain.getObservacoes())
                .createdAt(domain.getCreatedAt() != null ? domain.getCreatedAt() : LocalDateTime.now())
                .updatedAt(domain.getUpdatedAt() != null ? domain.getUpdatedAt() : LocalDateTime.now());
        
        ConfiguracaoPagamentoEntity entity = builder.build();
        
        if (domain.getUsuario() != null) {
            UsuarioEntity usuarioEntity = usuarioMapper.toEntity(domain.getUsuario());
            entity.setUsuario(usuarioEntity);
        } else if (domain.getUsuarioId() != null) {
            // Se não tiver o objeto Usuario mas tiver o ID, criar uma referência apenas com o ID
            UsuarioEntity usuarioEntity = new UsuarioEntity();
            usuarioEntity.setId(domain.getUsuarioId());
            entity.setUsuario(usuarioEntity);
        }

        return entity;
    }

    public ConfiguracaoPagamento toDomain(ConfiguracaoPagamentoEntity entity) {
        if (entity == null) {
            return null;
        }

        ConfiguracaoPagamento domain = ConfiguracaoPagamento.builder()
                .id(entity.getId())
                .posId(entity.getPosId())
                .merchantId(entity.getMerchantId())
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
                .ambiente(AmbienteEnum.getAmbiente(entity.getAmbiente()))
                .webhookUrl(entity.getWebhookUrl())
                .webhookSecret(entity.getWebhookSecret())
                .ativo(entity.getAtivo())
                .webhookAtivo(entity.getWebhookAtivo())
                .returnAtivo(entity.getReturnAtivo())
                .observacoes(entity.getObservacoes())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
        
        if (entity.getUsuario() != null) {
            try {
                Usuario usuario = usuarioMapper.toDomain(entity.getUsuario());
                if (usuario != null) {
                    domain.setUsuario(usuario);
                }
                // Sempre definir o ID do usuário, mesmo que o mapeamento falhe
                domain.setUsuarioId(entity.getUsuario().getId());
            } catch (Exception e) {
                // Se houver erro no mapeamento do usuário, pelo menos definir o ID
                domain.setUsuarioId(entity.getUsuario().getId());
            }
        }
        
        return domain;
    }
}
