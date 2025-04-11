package cv.ntinnovation.gateway_payment.adapter.out.db.mapper;

import cv.ntinnovation.gateway_payment.adapter.out.db.entity.IntencaoPagamentoEntity;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.ContaPagamento;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.IntencaoPagamento;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.MetodoPagamento;
import org.springframework.stereotype.Component;

@Component
public class IntencaoPagamentoMapper {

    private final ContaPagamentoMapper contaPagamentoMapper;
    private final MetodoPagamentoMapper metodoPagamentoMapper;

    public IntencaoPagamentoMapper(ContaPagamentoMapper contaPagamentoMapper, MetodoPagamentoMapper metodoPagamentoMapper) {
        this.contaPagamentoMapper = contaPagamentoMapper;
        this.metodoPagamentoMapper = metodoPagamentoMapper;
    }

    public IntencaoPagamento toIntencaoPagamento(IntencaoPagamentoEntity entity) {
        if (entity == null) {
            return null;
        }
        
        ContaPagamento contaPagamento = contaPagamentoMapper.toContaPagamento(entity.getContaPagamento());
        MetodoPagamento metodoPagamento = metodoPagamentoMapper.toMetodoPagamento(entity.getMetodoPagamento());
        
        return IntencaoPagamento.builder()
                .id(entity.getId())
                .amount(entity.getAmount())
                .createdAt(entity.getCreatedAt())
                .metodoPagamento(metodoPagamento)
                .contaPagamento(contaPagamento)
                .status(entity.getStatus())
                .reference(entity.getReference())
                .currency(entity.getCurrency())
                .merchantRef(entity.getMerchantRef())
                .merchantSession(entity.getMerchantSession())
                .posId(entity.getPosId())
                .entityCode(entity.getEntityCode())
                .transactionCode(entity.getTransactionCode())
                .referenceNumber(entity.getReferenceNumber())
                .build();
    }

    public IntencaoPagamentoEntity toIntencaoPagamentoEntity(IntencaoPagamento domain) {
        if (domain == null) {
            return null;
        }
        
        IntencaoPagamentoEntity entity = new IntencaoPagamentoEntity();
        entity.setId(domain.getId());
        entity.setAmount(domain.getAmount());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setMetodoPagamento(metodoPagamentoMapper.toMetodoPagamentoEntity(domain.getMetodoPagamento()));
        entity.setContaPagamento(contaPagamentoMapper.toContaPagamentoEntity(domain.getContaPagamento()));
        entity.setStatus(domain.getStatus());
        entity.setReference(domain.getReference());
        entity.setCurrency(domain.getCurrency());
        entity.setMerchantRef(domain.getMerchantRef());
        entity.setMerchantSession(domain.getMerchantSession());
        entity.setPosId(domain.getPosId());
        entity.setEntityCode(domain.getEntityCode());
        entity.setTransactionCode(domain.getTransactionCode());
        entity.setReferenceNumber(domain.getReferenceNumber());
        
        return entity;
    }
}
