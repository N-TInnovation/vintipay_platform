package cv.ntinnovation.vintipay.adapter.out.db.adapter;

import cv.ntinnovation.vintipay.adapter.out.db.mapper.IntencaoPagamentoMapper;
import cv.ntinnovation.vintipay.adapter.out.db.repository.IntencaoPagamentoRepository;
import cv.ntinnovation.vintipay.application.core.domain.entity.IntencaoPagamento;
import cv.ntinnovation.vintipay.application.port.out.BuscaIntencaoPagamentoPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BuscaIntencaoPagamentoAdapter implements BuscaIntencaoPagamentoPort {

    private final IntencaoPagamentoRepository intencaoPagamentoRepository;
    private final IntencaoPagamentoMapper intencaoPagamentoMapper;

    @Override
    public Optional<IntencaoPagamento> buscarPorReferencia(String reference) {
        return intencaoPagamentoRepository.findByReference(reference)
                .map(intencaoPagamentoMapper::toIntencaoPagamento);
    }
}
