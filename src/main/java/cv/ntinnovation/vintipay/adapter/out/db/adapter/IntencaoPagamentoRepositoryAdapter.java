package cv.ntinnovation.vintipay.adapter.out.db.adapter;

import cv.ntinnovation.vintipay.adapter.out.db.entity.IntencaoPagamentoEntity;
import cv.ntinnovation.vintipay.adapter.out.db.mapper.IntencaoPagamentoMapper;
import cv.ntinnovation.vintipay.adapter.out.db.repository.IntencaoPagamentoRepository;
import cv.ntinnovation.vintipay.application.core.domain.entity.IntencaoPagamento;
import cv.ntinnovation.vintipay.application.port.out.BuscaIntencaoPagamento;
import cv.ntinnovation.vintipay.application.port.out.SalvaIntencaoPagamento;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Adaptador para o repositório de intenções de pagamento
 */
@Component
@RequiredArgsConstructor
public class IntencaoPagamentoRepositoryAdapter implements BuscaIntencaoPagamento, SalvaIntencaoPagamento {

    private final IntencaoPagamentoRepository intencaoPagamentoRepository;
    private final IntencaoPagamentoMapper intencaoPagamentoMapper;

    @Override
    public IntencaoPagamento buscarPorId(String id) {
        try {
            Long idLong = Long.parseLong(id);
            return intencaoPagamentoRepository.findById(idLong)
                    .map(intencaoPagamentoMapper::toDomain)
                    .orElse(null);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public Optional<IntencaoPagamento> buscarPorReferencia(String referencia) {
        return intencaoPagamentoRepository.findByReferencia(referencia)
                .map(intencaoPagamentoMapper::toDomain);
    }

    @Override
    public IntencaoPagamento salvar(IntencaoPagamento intencaoPagamento) {
        IntencaoPagamentoEntity entity = intencaoPagamentoMapper.toEntity(intencaoPagamento);
        entity = intencaoPagamentoRepository.save(entity);
        return intencaoPagamentoMapper.toDomain(entity);
    }
}
