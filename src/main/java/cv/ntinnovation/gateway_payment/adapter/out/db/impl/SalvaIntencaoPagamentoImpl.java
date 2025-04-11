package cv.ntinnovation.gateway_payment.adapter.out.db.impl;

import cv.ntinnovation.gateway_payment.adapter.out.db.entity.IntencaoPagamentoEntity;
import cv.ntinnovation.gateway_payment.adapter.out.db.mapper.IntencaoPagamentoMapper;
import cv.ntinnovation.gateway_payment.adapter.out.db.repository.IntencaoPagamentoRepository;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.IntencaoPagamento;
import cv.ntinnovation.gateway_payment.application.port.out.SalvaIntencaoPagamento;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class SalvaIntencaoPagamentoImpl implements SalvaIntencaoPagamento {

    private final IntencaoPagamentoRepository intencaoPagamentoRepository;
    private final IntencaoPagamentoMapper intencaoPagamentoMapper;

    public SalvaIntencaoPagamentoImpl(IntencaoPagamentoRepository intencaoPagamentoRepository, 
                                     IntencaoPagamentoMapper intencaoPagamentoMapper) {
        this.intencaoPagamentoRepository = intencaoPagamentoRepository;
        this.intencaoPagamentoMapper = intencaoPagamentoMapper;
    }

    @Override
    public IntencaoPagamento salvar(IntencaoPagamento intencaoPagamento) {
        // Garantir que a data de criação seja definida
        if (intencaoPagamento.getCreatedAt() == null) {
            intencaoPagamento.setCreatedAt(LocalDateTime.now());
        }
        
        // Gerar referência única se não existir
        if (intencaoPagamento.getReference() == null || intencaoPagamento.getReference().isEmpty()) {
            intencaoPagamento.setReference(UUID.randomUUID().toString());
        }
        
        // Converter para entidade
        IntencaoPagamentoEntity entity = intencaoPagamentoMapper.toIntencaoPagamentoEntity(intencaoPagamento);
        
        // Salvar no banco de dados
        IntencaoPagamentoEntity savedEntity = intencaoPagamentoRepository.save(entity);
        
        // Converter de volta para o domínio
        return intencaoPagamentoMapper.toIntencaoPagamento(savedEntity);
    }
}
