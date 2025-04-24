package cv.ntinnovation.vintipay.application.core.usecase;

import cv.ntinnovation.vintipay.application.core.domain.entity.Pagamento;
import cv.ntinnovation.vintipay.application.port.in.BuscaHistoricoPagamentoUseCase;
import cv.ntinnovation.vintipay.application.port.out.BuscaPagamentoPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Implementação do caso de uso para buscar histórico de pagamentos
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BuscaHistoricoPagamentoUseCaseImpl implements BuscaHistoricoPagamentoUseCase {

    private final BuscaPagamentoPort buscaPagamentoPort;

    @Override
    public Page<Pagamento> buscarHistoricoPagamentos(Long usuarioId, String status, Pageable pageable) {
        log.info("Buscando histórico de pagamentos para o usuário ID: {}, status: {}", usuarioId, status);
        return buscaPagamentoPort.buscarPorUsuarioIdEStatus(usuarioId, status, pageable);
    }
}
