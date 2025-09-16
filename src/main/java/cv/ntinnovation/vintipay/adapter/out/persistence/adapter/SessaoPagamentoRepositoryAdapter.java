package cv.ntinnovation.vintipay.adapter.out.persistence.adapter;

import cv.ntinnovation.vintipay.adapter.out.persistence.entity.SessaoPagamentoEntity;
import cv.ntinnovation.vintipay.adapter.out.persistence.mapper.SessaoPagamentoMapper;
import cv.ntinnovation.vintipay.adapter.out.persistence.repository.SessaoPagamentoRepository;
import cv.ntinnovation.vintipay.application.core.domain.entity.SessaoPagamento;
import cv.ntinnovation.vintipay.application.port.out.SalvaSessaoPagamento;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Adaptador para o repositório de sessões de pagamento
 */
@Component
@RequiredArgsConstructor
public class SessaoPagamentoRepositoryAdapter implements SalvaSessaoPagamento {

    private final SessaoPagamentoRepository sessaoPagamentoRepository;
    private final SessaoPagamentoMapper sessaoPagamentoMapper;

    @Override
    public SessaoPagamento salvar(SessaoPagamento sessaoPagamento) {
        SessaoPagamentoEntity entity = sessaoPagamentoMapper.toEntity(sessaoPagamento);
        entity = sessaoPagamentoRepository.save(entity);
        return sessaoPagamentoMapper.toDomain(entity);
    }
}
