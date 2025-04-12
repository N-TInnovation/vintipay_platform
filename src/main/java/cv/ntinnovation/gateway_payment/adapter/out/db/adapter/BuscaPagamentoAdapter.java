package cv.ntinnovation.gateway_payment.adapter.out.db.adapter;

import cv.ntinnovation.gateway_payment.adapter.out.db.mapper.PagamentoMapper;
import cv.ntinnovation.gateway_payment.adapter.out.db.repository.PagamentoRepository;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.Pagamento;
import cv.ntinnovation.gateway_payment.application.port.out.BuscaPagamentoPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BuscaPagamentoAdapter implements BuscaPagamentoPort {

    private final PagamentoRepository pagamentoRepository;
    private final PagamentoMapper pagamentoMapper;

    @Override
    public Optional<Pagamento> buscarPorMerchantRef(String merchantRef) {
        return pagamentoRepository.findByIntencaoPagamentoMerchantRef(merchantRef)
                .map(pagamentoMapper::toDomain);
    }
}
