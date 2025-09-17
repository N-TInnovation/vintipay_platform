package cv.ntinnovation.vintipay.adapter.out.persistence.mapper;

import cv.ntinnovation.vintipay.adapter.out.persistence.entity.DescontoCicloEntity;
import cv.ntinnovation.vintipay.adapter.out.persistence.entity.ProdutoEntity;
import cv.ntinnovation.vintipay.application.core.domain.entity.DescontoCiclo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe responsável por mapear entre os objetos de domínio DescontoCiclo e as entidades DescontoCicloEntity
 */
@Component
public class DescontoCicloMapper {

    /**
     * Converte uma entidade de domínio para uma entidade JPA
     *
     * @param descontoCiclo Entidade de domínio
     * @param produto Entidade JPA do produto associado ao desconto
     * @return Entidade JPA
     */
    public DescontoCicloEntity toEntity(DescontoCiclo descontoCiclo, ProdutoEntity produto) {
        if (descontoCiclo == null) {
            return null;
        }
        
        return DescontoCicloEntity.builder()
                .id(descontoCiclo.getId())
                .numeroCiclo(descontoCiclo.getNumeroCiclo())
                .percentualDesconto(descontoCiclo.getPercentualDesconto())
                .descricao(descontoCiclo.getDescricao())
                .produto(produto)
                .build();
    }

    /**
     * Converte uma lista de entidades de domínio para uma lista de entidades JPA
     *
     * @param descontos Lista de entidades de domínio
     * @param produto Entidade JPA do produto associado aos descontos
     * @return Lista de entidades JPA
     */
    public List<DescontoCicloEntity> toEntityList(List<DescontoCiclo> descontos, ProdutoEntity produto) {
        if (descontos == null) {
            return List.of();
        }
        
        return descontos.stream()
                .map(desconto -> toEntity(desconto, produto))
                .collect(Collectors.toList());
    }

    /**
     * Converte uma entidade JPA para uma entidade de domínio
     *
     * @param entity Entidade JPA
     * @return Entidade de domínio
     */
    public DescontoCiclo toDomain(DescontoCicloEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return DescontoCiclo.builder()
                .id(entity.getId())
                .numeroCiclo(entity.getNumeroCiclo())
                .percentualDesconto(entity.getPercentualDesconto())
                .descricao(entity.getDescricao())
                .produtoId(entity.getProduto() != null ? entity.getProduto().getId() : null)
                .build();
    }

    /**
     * Converte uma lista de entidades JPA para uma lista de entidades de domínio
     *
     * @param entities Lista de entidades JPA
     * @return Lista de entidades de domínio
     */
    public List<DescontoCiclo> toDomainList(List<DescontoCicloEntity> entities) {
        if (entities == null) {
            return List.of();
        }
        
        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
}
