package cv.ntinnovation.vintipay.application.core.usecase;

import cv.ntinnovation.vintipay.application.core.domain.entity.ContaPagamento;
import cv.ntinnovation.vintipay.application.port.in.BuscaContaPagamentoUseCase;
import cv.ntinnovation.vintipay.application.port.out.BuscaContaPagamentoPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementação do caso de uso para busca de contas de pagamento (clientes)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BuscaContaPagamentoUseCaseImpl implements BuscaContaPagamentoUseCase {

    private final BuscaContaPagamentoPort buscaContaPagamentoPort;

    @Override
    public Page<ContaPagamento> buscarTodos(Pageable pageable) {
        log.debug("Buscando todas as contas de pagamento com paginação");
        return buscaContaPagamentoPort.buscarTodos(pageable);
    }

    @Override
    public Optional<ContaPagamento> buscarPorId(Long id) {
        log.debug("Buscando conta de pagamento por ID: {}", id);
        return buscaContaPagamentoPort.buscarPorId(id);
    }

    @Override
    public Page<ContaPagamento> buscarPorNomeOuEmail(String query, Pageable pageable) {
        log.debug("Buscando contas de pagamento por nome ou email contendo: {}", query);
        return buscaContaPagamentoPort.buscarPorNomeOuEmail(query, pageable);
    }
}
