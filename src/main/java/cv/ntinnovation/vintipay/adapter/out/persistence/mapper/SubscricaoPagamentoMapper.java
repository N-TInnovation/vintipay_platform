package cv.ntinnovation.vintipay.adapter.out.persistence.mapper;

import cv.ntinnovation.vintipay.adapter.out.persistence.entity.SubscricaoPagamentoEntity;
import cv.ntinnovation.vintipay.application.core.domain.entity.SubscricaoPagamento;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscricaoPagamentoMapper {

    private final IntencaoPagamentoMapper intencaoPagamentoMapper;
    private final ContaPagamentoMapper contaPagamentoMapper;
    private final TenantMapper tenantMapper;

    public SubscricaoPagamento toSubscricaoPagamento(SubscricaoPagamentoEntity entity) {
        return SubscricaoPagamento.builder()
                .createdAt(entity.getCreatedAt())
                .id(entity.getId())
                .status(entity.getStatus())
                .fingerprint(entity.getFingerprint())
                .panMascarado(entity.getPanMascarado())
                .resultFingerPrint(entity.getResultFingerPrint())
                .ciclo(entity.getCiclo())
                .contaPagamento(contaPagamentoMapper.toContaPagamento(entity.getContaPagamento()))
                .intencaoPagamento(intencaoPagamentoMapper.toIntencaoPagamento(entity.getIntencaoPagamentoAtual()))
                .reference(entity.getReference())
                .ambiente(entity.getAmbiente())
                .tenant(entity.getTenant() != null ? tenantMapper.toDomain(entity.getTenant()) : null)
                .tenantId(entity.getTenantId())
                .tokenDescription(entity.getTokenDescription())
                .maxNumberOfTransactions(entity.getMaxNumberOfTransactions())
                .tokenExpiryDate(entity.getTokenExpiryDate())
                .maxAmountAllowed(entity.getMaxAmountAllowed())
                .limitDate(entity.getLimitDate())
                .build();
    }

    public SubscricaoPagamentoEntity toSubscricaoPagamentoEntity(SubscricaoPagamento subscricaoPagamento) {
        var entity = new SubscricaoPagamentoEntity();
        entity.setId(subscricaoPagamento.getId());
        entity.setCreatedAt(subscricaoPagamento.getCreatedAt());
        entity.setStatus(subscricaoPagamento.getStatus());
        entity.setFingerprint(subscricaoPagamento.getFingerprint());
        entity.setPanMascarado(subscricaoPagamento.getPanMascarado());
        entity.setResultFingerPrint(subscricaoPagamento.getResultFingerPrint());
        entity.setCiclo(subscricaoPagamento.getCiclo());
        entity.setContaPagamento(contaPagamentoMapper.toContaPagamentoEntity(subscricaoPagamento.getContaPagamento()));
        entity.setIntencaoPagamentoAtual(intencaoPagamentoMapper.toIntencaoPagamentoEntity(subscricaoPagamento.getIntencaoPagamento()));
        entity.setAmbiente(subscricaoPagamento.getAmbiente());
        entity.setReference(subscricaoPagamento.getReference());
        entity.setTenant(subscricaoPagamento.getTenant() != null ? tenantMapper.toEntity(subscricaoPagamento.getTenant()) : null);
        entity.setTenantId(subscricaoPagamento.getTenantId());
        entity.setTokenDescription(subscricaoPagamento.getTokenDescription());
        entity.setMaxNumberOfTransactions(subscricaoPagamento.getMaxNumberOfTransactions());
        entity.setTokenExpiryDate(subscricaoPagamento.getTokenExpiryDate());
        entity.setMaxAmountAllowed(subscricaoPagamento.getMaxAmountAllowed());
        entity.setLimitDate(subscricaoPagamento.getLimitDate());
        return entity;
    }
}
