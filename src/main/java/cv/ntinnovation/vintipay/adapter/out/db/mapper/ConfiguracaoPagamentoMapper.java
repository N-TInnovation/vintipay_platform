package cv.ntinnovation.vintipay.adapter.out.db.mapper;

import cv.ntinnovation.vintipay.adapter.out.db.entity.ConfiguracaoPagamentoEntity;
import cv.ntinnovation.vintipay.adapter.out.db.entity.UsuarioEntity;
import cv.ntinnovation.vintipay.application.core.domain.entity.ConfiguracaoPagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.Usuario;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConfiguracaoPagamentoMapper {

    private final UsusarioMapper ususarioMapper;

    public ConfiguracaoPagamentoEntity toEntity(ConfiguracaoPagamento domain) {
        if (domain == null) {
            return null;
        }

        ConfiguracaoPagamentoEntity entity = ConfiguracaoPagamentoEntity.builder()
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
        
        if (domain.getUsuario() != null) {
            UsuarioEntity usuarioEntity = ususarioMapper.toEntity(domain.getUsuario());
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
        
        if (entity.getUsuario() != null) {
            Usuario usuario = ususarioMapper.toDomain(entity.getUsuario());
            domain.setUsuario(usuario);
        }
        
        return domain;
    }
}
