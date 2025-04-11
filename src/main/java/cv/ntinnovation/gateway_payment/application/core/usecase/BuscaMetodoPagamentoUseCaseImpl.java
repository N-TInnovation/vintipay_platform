package cv.ntinnovation.gateway_payment.application.core.usecase;

import cv.ntinnovation.gateway_payment.application.core.domain.entity.MetodoPagamento;
import cv.ntinnovation.gateway_payment.application.port.in.BuscaMetodoPagamentoUseCase;
import cv.ntinnovation.gateway_payment.application.port.out.BuscaMetodoPagamentoPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BuscaMetodoPagamentoUseCaseImpl implements BuscaMetodoPagamentoUseCase {

    private final BuscaMetodoPagamentoPort buscaMetodoPagamentoPort;

    @Override
    @Transactional(readOnly = true)
    public List<MetodoPagamento> listarTodos() {
        return buscaMetodoPagamentoPort.buscarTodos();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MetodoPagamento> buscarPorId(Long id) {
        return buscaMetodoPagamentoPort.buscarPorId(id);
    }
}
