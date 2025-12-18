package cv.ntinnovation.vintipay.adapter.out.persistence.mapper;

import cv.ntinnovation.vintipay.adapter.out.persistence.entity.PagamentoEntity;
import cv.ntinnovation.vintipay.adapter.out.persistence.entity.TenantEntity;
import cv.ntinnovation.vintipay.application.core.domain.entity.Pagamento;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PagamentoEntityMapper {

    private final IntencaoPagamentoMapper intencaoPagamentoMapper;

    public Pagamento toDomain(PagamentoEntity entity) {
        return Pagamento.builder()
                .id(entity.getId())
                .reference(entity.getReference())
                .fingerprint(entity.getFingerprint())
                .timestamp(entity.getTimestamp())
                .status(entity.getStatus())
                .mensagem(entity.getMensagem())
                .panMascarado(entity.getPanMascarado())
                .createdAt(entity.getCreatedAt())
                .resultFingerPrint(entity.getResultFingerprint())
                .intencaoPagamento(intencaoPagamentoMapper.toIntencaoPagamento(entity.getIntencaoPagamento()))
                .tenantId(entity.getTenant() != null ? entity.getTenant().getId() : null)
                .build();
    }

    public PagamentoEntity toEntity(Pagamento domain) {
        PagamentoEntity.PagamentoEntityBuilder builder = PagamentoEntity.builder()
                .id(domain.getId())
                .reference(domain.getReference())
                .fingerprint(domain.getFingerprint())
                .timestamp(domain.getTimestamp())
                .status(domain.getStatus())
                .mensagem(domain.getMensagem())
                .panMascarado(domain.getPanMascarado())
                .createdAt(domain.getCreatedAt())
                .resultFingerprint(domain.getResultFingerPrint())
                .intencaoPagamento(intencaoPagamentoMapper.toIntencaoPagamentoEntity(domain.getIntencaoPagamento()));
        
        PagamentoEntity entity = builder.build();
        
        if (domain.getTenant() != null) {
            TenantEntity tenantEntity = new TenantEntity();
            tenantEntity.setId(domain.getTenant().getId());
            entity.setTenant(tenantEntity);
        } else if (domain.getTenantId() != null) {
            TenantEntity tenantEntity = new TenantEntity();
            tenantEntity.setId(domain.getTenantId());
            entity.setTenant(tenantEntity);
        } else if (domain.getIntencaoPagamento() != null && domain.getIntencaoPagamento().getTenantId() != null) {
            TenantEntity tenantEntity = new TenantEntity();
            tenantEntity.setId(domain.getIntencaoPagamento().getTenantId());
            entity.setTenant(tenantEntity);
        } else {
            throw new IllegalArgumentException("Tenant é obrigatório para criar um pagamento");
        }
        
        return entity;
    }
}
