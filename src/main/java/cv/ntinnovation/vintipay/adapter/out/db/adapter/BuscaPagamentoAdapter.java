package cv.ntinnovation.vintipay.adapter.out.db.adapter;

import cv.ntinnovation.vintipay.adapter.out.db.mapper.PagamentoMapper;
import cv.ntinnovation.vintipay.adapter.out.db.repository.PagamentoRepository;
import cv.ntinnovation.vintipay.application.core.domain.entity.Pagamento;
import cv.ntinnovation.vintipay.application.core.domain.enums.StatusPagamentoEnum;
import cv.ntinnovation.vintipay.application.port.out.BuscaPagamentoPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BuscaPagamentoAdapter implements BuscaPagamentoPort {

    private final PagamentoRepository pagamentoRepository;
    private final PagamentoMapper pagamentoMapper;

    @Override
    public Page<Pagamento> buscarTodos(Pageable pageable, String reference, StatusPagamentoEnum status) {
        var pagamentos = reference != null && !reference.isEmpty() ?
                pagamentoRepository.findByReferenceContainingIgnoreCaseAndStatusIfPresent(reference, status, pageable) :
                pagamentoRepository.findByStatusIfPresent(status, pageable);
        return pagamentos.map(pagamentoMapper::toDomain);
    }

    @Override
    public Optional<Pagamento> buscarPorReferencia(String reference) {
        return pagamentoRepository.findByReference(reference)
                .map(pagamentoMapper::toDomain);
    }
}
