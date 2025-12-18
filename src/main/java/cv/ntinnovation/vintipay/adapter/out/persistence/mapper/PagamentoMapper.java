package cv.ntinnovation.vintipay.adapter.out.persistence.mapper;


import cv.ntinnovation.vintipay.adapter.out.persistence.entity.PagamentoEntity;
import cv.ntinnovation.vintipay.adapter.out.persistence.entity.TenantEntity;
import cv.ntinnovation.vintipay.application.core.domain.entity.Pagamento;
import cv.ntinnovation.vintipay.application.core.domain.enums.AmbienteEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PagamentoMapper {

    private final IntencaoPagamentoMapper intencaoPagamentoMapper;

    public Pagamento toDomain(PagamentoEntity entity) {
        return Pagamento.builder()
                .id(entity.getId())
                .intencaoPagamento(intencaoPagamentoMapper.toIntencaoPagamento(entity.getIntencaoPagamento()))
                .fingerprint(entity.getFingerprint())
                .timestamp(entity.getTimestamp())
                .authorizationCode(entity.getAuthorizationCode())
                .status(entity.getStatus())
                .mensagem(entity.getMensagem())
                .reference(entity.getReference())
                .panMascarado(entity.getPanMascarado())
                .createdAt(entity.getCreatedAt())
                .transactionId(entity.getTransactionId())
                .clearingPeriod(entity.getClearingPeriod())
                .ambiente(AmbienteEnum.fromAmbiente(entity.getAmbiente()))
                .tenantId(entity.getTenant() != null ? entity.getTenant().getId() : null)
                .build();
    }

    public PagamentoEntity toEntity(Pagamento pagamento) {
        PagamentoEntity.PagamentoEntityBuilder builder = PagamentoEntity.builder()
                .id(pagamento.getId())
                .intencaoPagamento(intencaoPagamentoMapper.toIntencaoPagamentoEntity(pagamento.getIntencaoPagamento()))
                .fingerprint(pagamento.getFingerprint())
                .timestamp(pagamento.getTimestamp())
                .mensagem(pagamento.getMensagem())
                .reference(pagamento.getReference())
                .panMascarado(pagamento.getPanMascarado())
                .createdAt(LocalDateTime.now())
                .status(pagamento.getStatus())
                .authorizationCode(pagamento.getAuthorizationCode())
                .transactionId(pagamento.getTransactionId())
                .clearingPeriod(pagamento.getClearingPeriod())
                .ambiente(pagamento.getAmbiente().getAmbiente());
        
        PagamentoEntity entity = builder.build();
        
        if (pagamento.getTenant() != null) {
            TenantEntity tenantEntity = new TenantEntity();
            tenantEntity.setId(pagamento.getTenant().getId());
            entity.setTenant(tenantEntity);
        } else if (pagamento.getTenantId() != null) {
            TenantEntity tenantEntity = new TenantEntity();
            tenantEntity.setId(pagamento.getTenantId());
            entity.setTenant(tenantEntity);
        } else if (pagamento.getIntencaoPagamento() != null && pagamento.getIntencaoPagamento().getTenantId() != null) {
            TenantEntity tenantEntity = new TenantEntity();
            tenantEntity.setId(pagamento.getIntencaoPagamento().getTenantId());
            entity.setTenant(tenantEntity);
        } else {
            throw new IllegalArgumentException("Tenant é obrigatório para criar um pagamento");
        }
        
        return entity;
    }
}
