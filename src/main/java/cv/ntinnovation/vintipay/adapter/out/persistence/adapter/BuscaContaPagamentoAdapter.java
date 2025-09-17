package cv.ntinnovation.vintipay.adapter.out.persistence.adapter;

import cv.ntinnovation.vintipay.adapter.out.persistence.mapper.BuscaContaPagamentoMapper;
import cv.ntinnovation.vintipay.adapter.out.persistence.repository.ContaPagamentoRepository;
import cv.ntinnovation.vintipay.application.core.domain.entity.ContaPagamento;
import cv.ntinnovation.vintipay.application.port.out.BuscaContaPagamentoPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Adapter para integrar o repositório com a porta de saída para busca de contas de pagamento
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BuscaContaPagamentoAdapter implements BuscaContaPagamentoPort {

    private final ContaPagamentoRepository contaPagamentoRepository;
    private final BuscaContaPagamentoMapper buscaContaPagamentoMapper;

    @Override
    public Page<ContaPagamento> buscarTodos(Pageable pageable) {
        var entidades = contaPagamentoRepository.findAll(pageable);
        return buscaContaPagamentoMapper.toContaPagamentoPage(entidades);
    }

    @Override
    public Optional<ContaPagamento> buscarPorId(Long id) {
        return contaPagamentoRepository.findById(id)
                .map(buscaContaPagamentoMapper::toContaPagamento);
    }

    @Override
    public Optional<ContaPagamento> buscarPorEmail(String email) {
        return contaPagamentoRepository.findByEmail(email)
                .map(buscaContaPagamentoMapper::toContaPagamento);
    }

    @Override
    public Page<ContaPagamento> buscarPorNomeOuEmail(String query, Pageable pageable) {
        var entidades = contaPagamentoRepository.findByNameOrEmailContainingIgnoreCase(query, pageable);
        return buscaContaPagamentoMapper.toContaPagamentoPage(entidades);
    }

    @Override
    public long contarTodos() {
        return contaPagamentoRepository.count();
    }
}
