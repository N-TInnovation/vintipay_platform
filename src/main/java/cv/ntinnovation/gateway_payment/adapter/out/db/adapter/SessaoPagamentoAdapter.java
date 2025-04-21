package cv.ntinnovation.gateway_payment.adapter.out.db.adapter;

import cv.ntinnovation.gateway_payment.adapter.out.db.mapper.SessaoPagamentoMapper;
import cv.ntinnovation.gateway_payment.adapter.out.db.repository.SessaoPagamentoRepository;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.SessaoPagamento;
import cv.ntinnovation.gateway_payment.application.port.out.BuscaSessaoPagamentoPort;
import cv.ntinnovation.gateway_payment.application.port.out.SalvaSessaoPagamentoPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SessaoPagamentoAdapter implements SalvaSessaoPagamentoPort, BuscaSessaoPagamentoPort {

    private final SessaoPagamentoRepository repository;
    private final SessaoPagamentoMapper mapper;

    @Override
    public SessaoPagamento salvar(SessaoPagamento sessaoPagamento) {
        var entity = mapper.toEntity(sessaoPagamento);
        var savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<SessaoPagamento> buscarPorSessionId(String sessionId) {
        return repository.findBySessionId(sessionId)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<SessaoPagamento> buscarSessaoValidaPorReferencia(String reference) {
        return repository.findValidSessionByReference(reference, LocalDateTime.now())
                .map(mapper::toDomain);
    }

    @Override
    public List<SessaoPagamento> buscarPorClientId(String clientId) {
        return repository.findByClientId(clientId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<SessaoPagamento> buscarSessoesExpiradas() {
        return repository.findExpiredSessions(LocalDateTime.now()).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
