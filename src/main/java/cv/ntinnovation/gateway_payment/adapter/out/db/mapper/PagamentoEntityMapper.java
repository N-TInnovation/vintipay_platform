package cv.ntinnovation.gateway_payment.adapter.out.db.mapper;

import cv.ntinnovation.gateway_payment.adapter.out.db.entity.PagamentoEntity;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.Pagamento;
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
                .build();
    }

    public PagamentoEntity toEntity(Pagamento domain) {
        return PagamentoEntity.builder()
                .id(domain.getId())
                .reference(domain.getReference())
                .fingerprint(domain.getFingerprint())
                .timestamp(domain.getTimestamp())
                .status(domain.getStatus())
                .mensagem(domain.getMensagem())
                .panMascarado(domain.getPanMascarado())
                .createdAt(domain.getCreatedAt())
                .resultFingerprint(domain.getResultFingerPrint())
                .intencaoPagamento(intencaoPagamentoMapper.toIntencaoPagamentoEntity(domain.getIntencaoPagamento()))
                .build();
    }
}
