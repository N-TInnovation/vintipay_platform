package cv.ntinnovation.gateway_payment.adapter.out.db.adapter;

import cv.ntinnovation.gateway_payment.adapter.out.db.mapper.PagamentoEntityMapper;
import cv.ntinnovation.gateway_payment.adapter.out.db.repository.PagamentoRepository;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.Pagamento;
import cv.ntinnovation.gateway_payment.application.port.out.BuscaPagamentoPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BuscaPagamentoAdapter implements BuscaPagamentoPort {

    private final PagamentoRepository pagamentoRepository;
    private final PagamentoEntityMapper pagamentoEntityMapper;

    @Override
    public Optional<Pagamento> buscarPorReferencia(String reference) {
        return pagamentoRepository.findByIntencaoPagamentoReference(reference)
                .map(pagamentoEntityMapper::toDomain);
    }

    @Override
    public Page<Pagamento> buscarTodos(Pageable pageable) {
        return pagamentoRepository.findAll(pageable)
                .map(pagamentoEntityMapper::toDomain);
    }
}
