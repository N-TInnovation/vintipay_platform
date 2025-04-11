package cv.ntinnovation.gateway_payment.adapter.out.db.adapter;

import cv.ntinnovation.gateway_payment.adapter.out.db.mapper.PagamentoMapper;
import cv.ntinnovation.gateway_payment.adapter.out.db.repository.PagamentoRepository;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.Pagamento;
import cv.ntinnovation.gateway_payment.application.port.out.SalvaPagamento;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PagamentoAdapter implements SalvaPagamento {

    private final PagamentoRepository pagamentoRepository;
    private final PagamentoMapper pagamentoMapper;

    @Override
    public Pagamento salvar(Pagamento pagamento) {
        var entity = pagamentoMapper.toEntity(pagamento);
        var saved = pagamentoRepository.save(entity);
        return pagamentoMapper.toDomain(saved);
    }
}
