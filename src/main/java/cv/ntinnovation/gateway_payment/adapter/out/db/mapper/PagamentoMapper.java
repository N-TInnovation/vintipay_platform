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

    public Pagamento toDomain(PagamentoEntity entity) {
        return Pagamento.builder()
                .id(entity.getId())
                .intencaoPagamento(intencaoPagamentoMapper.toIntencaoPagamento(entity.getIntencaoPagamento()))
                .fingerprint(entity.getFingerprint())
                .timestamp(entity.getTimestamp())
                .status(entity.getIntencaoPagamento().getStatus())
                .mensagem(entity.getMensagem())
                .codigoAutorizacao(entity.getCodigoAutorizacao())
                .panMascarado(entity.getPanMascarado())
                .createdAt(entity.getCreatedAt())
                .reference(entity.getIntencaoPagamento().getReference())
                .build();
    }

    public PagamentoEntity toEntity(Pagamento pagamento) {
        return PagamentoEntity.builder()
                .intencaoPagamento(intencaoPagamentoMapper.toIntencaoPagamentoEntity(pagamento.getIntencaoPagamento()))
                .fingerprint(pagamento.getFingerprint())
                .timestamp(pagamento.getTimestamp())
                .mensagem(pagamento.getMensagem())
                .codigoAutorizacao(pagamento.getCodigoAutorizacao())
                .panMascarado(pagamento.getPanMascarado())
                .createdAt(LocalDateTime.now())
                .status(pagamento.getStatus())
                .build();
    }
}
