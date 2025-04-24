package cv.ntinnovation.vintipay.adapter.out.db.adapter;

import cv.ntinnovation.vintipay.adapter.out.db.mapper.PagamentoMapper;
import cv.ntinnovation.vintipay.adapter.out.db.repository.PagamentoRepository;
import cv.ntinnovation.vintipay.application.core.domain.entity.Pagamento;
import cv.ntinnovation.vintipay.application.core.domain.enums.StatusPagamentoEnum;
import cv.ntinnovation.vintipay.application.port.out.BuscaPagamentoPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
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
    
    @Override
    public Page<Pagamento> buscarPorUsuario(Pageable pageable, Long usuarioId, StatusPagamentoEnum status) {
        // Converter o enum para string apenas se não for nulo
        String statusStr = status != null ? status.name() : null;
        
        try {
            var pagamentos = pagamentoRepository.findByUsuarioIdAndStatusIfPresent(usuarioId, statusStr, pageable);
            return pagamentos.map(pagamentoMapper::toDomain);
        } catch (Exception e) {
            // Em caso de erro na consulta, retornar uma página vazia
            return Page.empty(pageable);
        }
    }
    
    @Override
    public Page<Pagamento> buscarPorUsuarioIdEStatus(Long usuarioId, String status, Pageable pageable) {
        log.info("Buscando pagamentos para usuário ID: {}, status: {}", usuarioId, status);
        try {
            var pagamentos = pagamentoRepository.findByUsuarioIdAndStatusIfPresent(usuarioId, status, pageable);
            log.info("Encontrados {} pagamentos", pagamentos.getTotalElements());
            return pagamentos.map(pagamentoMapper::toDomain);
        } catch (Exception e) {
            log.error("Erro ao buscar pagamentos para usuário ID: {}, status: {}", usuarioId, status, e);
            return Page.empty(pageable);
        }
    }
}
