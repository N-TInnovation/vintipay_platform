package cv.ntinnovation.vintipay.adapter.out.persistence.mapper;

import cv.ntinnovation.vintipay.adapter.out.persistence.entity.PapelEntity;
import cv.ntinnovation.vintipay.adapter.out.persistence.entity.PermissaoEntity;
import cv.ntinnovation.vintipay.application.core.domain.entity.Papel;
import cv.ntinnovation.vintipay.application.core.domain.entity.Permissao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PapelMapper {

    private final PermissaoMapper permissaoMapper;

    public Papel toDomain(PapelEntity entity) {
        if (entity == null) {
            return null;
        }
        
        Papel papel = Papel.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .descricao(entity.getDescricao())
                .codigo(entity.getCodigo())
                .padrao(entity.isPadrao())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
        
        // Mapear permissões
        if (entity.getPermissoes() != null && !entity.getPermissoes().isEmpty()) {
            Set<Permissao> permissoes = entity.getPermissoes().stream()
                    .map(permissaoMapper::toDomain)
                    .collect(Collectors.toSet());
            papel.setPermissoes(permissoes);
        }
        
        return papel;
    }
    
    public PapelEntity toEntity(Papel domain) {
        if (domain == null) {
            return null;
        }
        
        PapelEntity entity = PapelEntity.builder()
                .id(domain.getId())
                .nome(domain.getNome())
                .descricao(domain.getDescricao())
                .codigo(domain.getCodigo())
                .padrao(domain.isPadrao())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
        
        // Mapear permissões
        if (domain.getPermissoes() != null && !domain.getPermissoes().isEmpty()) {
            Set<PermissaoEntity> permissoes = domain.getPermissoes().stream()
                    .map(permissaoMapper::toEntity)
                    .collect(Collectors.toSet());
            entity.setPermissoes(permissoes);
        }
        
        return entity;
    }
}
