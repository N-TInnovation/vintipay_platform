package cv.ntinnovation.vintipay.adapter.in.web.mapper;

import cv.ntinnovation.vintipay.adapter.in.web.dto.DescontoCicloRequest;
import cv.ntinnovation.vintipay.adapter.in.web.dto.DescontoCicloResponse;
import cv.ntinnovation.vintipay.application.core.domain.entity.DescontoCiclo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para converter entre objetos de domínio DescontoCiclo e DTOs
 */
@Component
public class DescontoCicloWebMapper {

    /**
     * Converte um DTO de requisição para um objeto de domínio
     *
     * @param request DTO de requisição
     * @return Objeto de domínio
     */
    public DescontoCiclo toDomain(DescontoCicloRequest request) {
        if (request == null) {
            return null;
        }

        return DescontoCiclo.builder()
                .id(request.getId())
                .numeroCiclo(request.getNumeroCiclo())
                .percentualDesconto(request.getPercentualDesconto())
                .descricao(request.getDescricao())
                .build();
    }

    /**
     * Converte uma lista de DTOs de requisição para uma lista de objetos de domínio
     *
     * @param requests Lista de DTOs de requisição
     * @return Lista de objetos de domínio
     */
    public List<DescontoCiclo> toDomainList(List<DescontoCicloRequest> requests) {
        if (requests == null) {
            return List.of();
        }

        return requests.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * Converte um objeto de domínio para um DTO de requisição
     *
     * @param domain Objeto de domínio
     * @return DTO de requisição
     */
    public DescontoCicloRequest toRequest(DescontoCiclo domain) {
        if (domain == null) {
            return null;
        }

        return DescontoCicloRequest.builder()
                .id(domain.getId())
                .numeroCiclo(domain.getNumeroCiclo())
                .percentualDesconto(domain.getPercentualDesconto())
                .descricao(domain.getDescricao())
                .build();
    }

    /**
     * Converte uma lista de objetos de domínio para uma lista de DTOs de requisição
     *
     * @param domains Lista de objetos de domínio
     * @return Lista de DTOs de requisição
     */
    public List<DescontoCicloRequest> toRequestList(List<DescontoCiclo> domains) {
        if (domains == null) {
            return List.of();
        }

        return domains.stream()
                .map(this::toRequest)
                .collect(Collectors.toList());
    }

    /**
     * Converte um objeto de domínio para um DTO de resposta
     *
     * @param domain Objeto de domínio
     * @return DTO de resposta
     */
    public DescontoCicloResponse toResponse(DescontoCiclo domain) {
        if (domain == null) {
            return null;
        }

        return DescontoCicloResponse.builder()
                .id(domain.getId())
                .numeroCiclo(domain.getNumeroCiclo())
                .percentualDesconto(domain.getPercentualDesconto())
                .descricao(domain.getDescricao())
                .produtoId(domain.getProdutoId())
                .build();
    }

    /**
     * Converte uma lista de objetos de domínio para uma lista de DTOs de resposta
     *
     * @param domains Lista de objetos de domínio
     * @return Lista de DTOs de resposta
     */
    public List<DescontoCicloResponse> toResponseList(List<DescontoCiclo> domains) {
        if (domains == null) {
            return List.of();
        }

        return domains.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
