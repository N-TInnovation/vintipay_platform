package cv.ntinnovation.gateway_payment.adapter.out.db.mapper;

import cv.ntinnovation.gateway_payment.adapter.out.db.entity.PagamentoEntity;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.Pagamento;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PagamentoMapper {

    private final IntencaoPagamentoMapper intencaoPagamentoMapper;

    public PagamentoEntity toEntity(Pagamento pagamento) {
        return PagamentoEntity.builder()
                .intencaoPagamento(intencaoPagamentoMapper.toIntencaoPagamentoEntity(pagamento.getIntencaoPagamento()))
                .fingerprint(pagamento.getFingerprint())
                .timestamp(pagamento.getTimestamp())
                .status("PENDENTE")
                .createdAt(LocalDateTime.now())
                .build();
    }

    public Pagamento toDomain(PagamentoEntity entity) {
        return Pagamento.builder()
                .id(entity.getId())
                .intencaoPagamento(intencaoPagamentoMapper.toIntencaoPagamento(entity.getIntencaoPagamento()))
                .fingerprint(entity.getFingerprint())
                .timestamp(entity.getTimestamp())
                .build();
    }
}
