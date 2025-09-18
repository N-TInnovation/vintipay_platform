package cv.ntinnovation.vintipay.adapter.out.persistence;

import cv.ntinnovation.vintipay.adapter.out.persistence.entity.SubscricaoPagamentoEntity;
import cv.ntinnovation.vintipay.adapter.out.persistence.mapper.SubscricaoPagamentoMapper;
import cv.ntinnovation.vintipay.adapter.out.persistence.repository.SubscricaoPagamentoRepository;
import cv.ntinnovation.vintipay.application.core.domain.entity.SubscricaoPagamento;
import cv.ntinnovation.vintipay.application.port.out.SalvaSubscricaoPagamentoPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SalvaSubscricaoPagamentoAdapter implements SalvaSubscricaoPagamentoPort {

    private final SubscricaoPagamentoRepository subscricaoPagamentoRepository;
    private final SubscricaoPagamentoMapper subscricaoPagamentoMapper;

    @Override
    public SubscricaoPagamento salvar(SubscricaoPagamento subscricaoPagamento) {
        var entity = subscricaoPagamentoMapper.toSubscricaoPagamentoEntity(subscricaoPagamento);
        var savedEntity = subscricaoPagamentoRepository.save(entity);
        return subscricaoPagamentoMapper.toSubscricaoPagamento(savedEntity);
    }

    @Override
    public Optional<SubscricaoPagamento> buscarPorIntencaoPagamentoId(Long intencaoPagamentoId) {
        return subscricaoPagamentoRepository.findByIntencaoPagamentoAtualId(intencaoPagamentoId)
                .map(subscricaoPagamentoMapper::toSubscricaoPagamento);
    }
}
