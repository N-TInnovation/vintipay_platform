package cv.ntinnovation.vintipay.adapter.in.web.mapper;

import cv.ntinnovation.vintipay.adapter.in.web.dto.PermissaoRequest;
import cv.ntinnovation.vintipay.adapter.in.web.dto.PermissaoResponse;
import cv.ntinnovation.vintipay.application.core.domain.entity.Permissao;
import org.springframework.stereotype.Component;

@Component
public class PermissaoWebMapper {

    public Permissao toDomain(PermissaoRequest request) {
        if (request == null) {
            return null;
        }
        
        return Permissao.builder()
                .id(request.getId())
                .nome(request.getNome())
                .descricao(request.getDescricao())
                .codigo(request.getCodigo())
                .build();
    }
    
    public PermissaoRequest toRequest(Permissao domain) {
        if (domain == null) {
            return null;
        }
        
        return PermissaoRequest.builder()
                .id(domain.getId())
                .nome(domain.getNome())
                .descricao(domain.getDescricao())
                .codigo(domain.getCodigo())
                .build();
    }
    
    public PermissaoResponse toResponse(Permissao domain) {
        if (domain == null) {
            return null;
        }
        
        return PermissaoResponse.builder()
                .id(domain.getId())
                .nome(domain.getNome())
                .descricao(domain.getDescricao())
                .codigo(domain.getCodigo())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
