package cv.ntinnovation.vintipay.adapter.out.persistence.mapper;

import cv.ntinnovation.vintipay.adapter.out.persistence.entity.PermissaoEntity;
import cv.ntinnovation.vintipay.application.core.domain.entity.Permissao;
import org.springframework.stereotype.Component;

@Component
public class PermissaoMapper {

    public Permissao toDomain(PermissaoEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return Permissao.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .descricao(entity.getDescricao())
                .codigo(entity.getCodigo())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
    
    public PermissaoEntity toEntity(Permissao domain) {
        if (domain == null) {
            return null;
        }
        
        return PermissaoEntity.builder()
                .id(domain.getId())
                .nome(domain.getNome())
                .descricao(domain.getDescricao())
                .codigo(domain.getCodigo())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
