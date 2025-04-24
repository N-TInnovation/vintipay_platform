package cv.ntinnovation.vintipay.application.core.usecase;

import cv.ntinnovation.vintipay.application.core.domain.entity.Pagamento;
import cv.ntinnovation.vintipay.application.core.domain.enums.StatusPagamentoEnum;
import cv.ntinnovation.vintipay.application.port.in.BuscaPagamentoUseCase;
import cv.ntinnovation.vintipay.application.port.out.BuscaPagamentoPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BuscaPagamentoUseCaseImpl implements BuscaPagamentoUseCase {

    private final BuscaPagamentoPort buscaPagamentoPort;

    @Override
    public Page<Pagamento> buscarTodos(Pageable pageable, String reference, StatusPagamentoEnum status) {
        return buscaPagamentoPort.buscarTodos(pageable, reference, status);
    }

    @Override
    public Optional<Pagamento> buscarPorReferencia(String reference) {
        return buscaPagamentoPort.buscarPorReferencia(reference);
    }
    
    @Override
    public Page<Pagamento> buscarPorUsuario(Pageable pageable, Long usuarioId, StatusPagamentoEnum status) {
        return buscaPagamentoPort.buscarPorUsuario(pageable, usuarioId, status);
    }
}
