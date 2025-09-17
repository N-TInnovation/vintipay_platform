package cv.ntinnovation.vintipay.adapter.out.persistence.mapper;

import cv.ntinnovation.vintipay.adapter.out.persistence.entity.ContaPagamentoEntity;
import cv.ntinnovation.vintipay.application.core.domain.entity.ContaPagamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Mapper para conversão entre entidades de persistência e objetos de domínio
 * relacionados à busca de contas de pagamento
 */
@Component
public class BuscaContaPagamentoMapper {

    private final ContaPagamentoMapper contaPagamentoMapper;

    public BuscaContaPagamentoMapper(ContaPagamentoMapper contaPagamentoMapper) {
        this.contaPagamentoMapper = contaPagamentoMapper;
    }

    /**
     * Converte uma entidade ContaPagamentoEntity para o objeto de domínio ContaPagamento
     * 
     * @param entity Entidade a ser convertida
     * @return Objeto de domínio ContaPagamento
     */
    public ContaPagamento toContaPagamento(ContaPagamentoEntity entity) {
        if (entity == null) {
            return null;
        }
        return contaPagamentoMapper.toContaPagamento(entity);
    }

    /**
     * Converte uma página de entidades ContaPagamentoEntity para uma página de objetos de domínio ContaPagamento
     * 
     * @param entityPage Página de entidades a ser convertida
     * @return Página de objetos de domínio ContaPagamento
     */
    public Page<ContaPagamento> toContaPagamentoPage(Page<ContaPagamentoEntity> entityPage) {
        if (entityPage == null) {
            return Page.empty();
        }
        
        var contasPagamento = entityPage.getContent().stream()
                .map(this::toContaPagamento)
                .collect(Collectors.toList());
        
        return new PageImpl<>(
                contasPagamento, 
                entityPage.getPageable(), 
                entityPage.getTotalElements()
        );
    }
}
