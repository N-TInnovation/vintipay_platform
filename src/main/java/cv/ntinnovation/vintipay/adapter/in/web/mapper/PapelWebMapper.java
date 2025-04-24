package cv.ntinnovation.vintipay.adapter.in.web.mapper;

import cv.ntinnovation.vintipay.adapter.in.web.dto.PapelRequest;
import cv.ntinnovation.vintipay.adapter.in.web.dto.PapelResponse;
import cv.ntinnovation.vintipay.application.core.domain.entity.Papel;
import cv.ntinnovation.vintipay.application.core.domain.entity.Permissao;
import cv.ntinnovation.vintipay.application.port.in.GerenciaPermissaoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PapelWebMapper {

    private final PermissaoWebMapper permissaoWebMapper;
    private final GerenciaPermissaoUseCase gerenciaPermissaoUseCase;

    public Papel toDomain(PapelRequest request) {
        if (request == null) {
            return null;
        }
        
        Papel papel = Papel.builder()
                .id(request.getId())
                .nome(request.getNome())
                .descricao(request.getDescricao())
                .codigo(request.getCodigo())
                .padrao(request.isPadrao())
                .build();
        
        // Mapear IDs de permissões para objetos Permissao
        if (request.getPermissoesIds() != null && !request.getPermissoesIds().isEmpty()) {
            Set<Permissao> permissoes = new HashSet<>();
            
            for (Long permissaoId : request.getPermissoesIds()) {
                gerenciaPermissaoUseCase.buscarPorId(permissaoId).ifPresent(permissoes::add);
            }
            
            papel.setPermissoes(permissoes);
        }
        
        return papel;
    }
    
    public PapelRequest toRequest(Papel domain) {
        if (domain == null) {
            return null;
        }
        
        PapelRequest request = PapelRequest.builder()
                .id(domain.getId())
                .nome(domain.getNome())
                .descricao(domain.getDescricao())
                .codigo(domain.getCodigo())
                .padrao(domain.isPadrao())
                .build();
        
        // Mapear permissões para IDs
        if (domain.getPermissoes() != null && !domain.getPermissoes().isEmpty()) {
            Set<Long> permissoesIds = domain.getPermissoes().stream()
                    .map(Permissao::getId)
                    .collect(Collectors.toSet());
            
            request.setPermissoesIds(permissoesIds);
        }
        
        return request;
    }
    
    public PapelResponse toResponse(Papel domain) {
        if (domain == null) {
            return null;
        }
        
        PapelResponse response = PapelResponse.builder()
                .id(domain.getId())
                .nome(domain.getNome())
                .descricao(domain.getDescricao())
                .codigo(domain.getCodigo())
                .padrao(domain.isPadrao())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
        
        // Mapear permissões para respostas
        if (domain.getPermissoes() != null && !domain.getPermissoes().isEmpty()) {
            Set<cv.ntinnovation.vintipay.adapter.in.web.dto.PermissaoResponse> permissoes = domain.getPermissoes().stream()
                    .map(permissaoWebMapper::toResponse)
                    .collect(Collectors.toSet());
            
            response.setPermissoes(permissoes);
        }
        
        return response;
    }
}
