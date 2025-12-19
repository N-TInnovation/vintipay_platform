package cv.ntinnovation.vintipay.adapter.out.persistence;

import cv.ntinnovation.vintipay.adapter.out.persistence.mapper.SubscricaoPagamentoMapper;
import cv.ntinnovation.vintipay.adapter.out.persistence.repository.SubscricaoPagamentoRepository;
import cv.ntinnovation.vintipay.application.core.domain.entity.SubscricaoPagamento;
import cv.ntinnovation.vintipay.application.core.domain.enums.AmbienteEnum;
import cv.ntinnovation.vintipay.application.core.domain.enums.StatusSubscricaoPagamentoEnum;
import cv.ntinnovation.vintipay.application.port.in.BuscaSubscricaoPagamentoUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BuscaSubscricaoPagamentoAdapter implements BuscaSubscricaoPagamentoUseCase {

    private final SubscricaoPagamentoRepository repository;
    private final SubscricaoPagamentoMapper mapper;

    @Override
    public Page<SubscricaoPagamento> buscarPorTenantEAmbiente(Pageable pageable, Long tenantId, AmbienteEnum ambiente) {
        log.info("Buscando assinaturas para tenant: {} e ambiente: {}", tenantId, ambiente);
        
        var entities = repository.findByTenantIdAndAmbiente(tenantId, ambiente, pageable);
        
        return entities.map(mapper::toSubscricaoPagamento);
    }

    @Override
    public Page<SubscricaoPagamento> buscarPorTenantEAmbienteEStatus(Pageable pageable, Long tenantId, StatusSubscricaoPagamentoEnum status, AmbienteEnum ambiente) {
        log.info("Buscando assinaturas para tenant: {}, status: {} e ambiente: {}", tenantId, status, ambiente);
        
        var entities = repository.findByTenantIdAndAmbienteAndStatus(tenantId, ambiente, status, pageable);
        
        return entities.map(mapper::toSubscricaoPagamento);
    }
}
