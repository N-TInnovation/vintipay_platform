package cv.ntinnovation.gateway_payment.adapter.out.db.impl;

import org.springframework.stereotype.Component;

import cv.ntinnovation.gateway_payment.adapter.out.db.entity.ContaPagamentoEntity;
import cv.ntinnovation.gateway_payment.adapter.out.db.mapper.ContaPagamentoMapper;
import cv.ntinnovation.gateway_payment.adapter.out.db.repository.ContaPagamentoRepository;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.ContaPagamento;
import cv.ntinnovation.gateway_payment.application.port.out.BuscaContaPagamentoPorEmail;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BuscaContaPagamentoPorEmailImpl implements BuscaContaPagamentoPorEmail {

    private final ContaPagamentoRepository contaPagamentoRepository;
    private final ContaPagamentoMapper contaPagamentoMapper;

    @Override
    public ContaPagamento buscarPorEmail(String email) {
        ContaPagamentoEntity entity = contaPagamentoRepository.findByEmail(email);
        if (entity == null) {
            return null;
        }
        return contaPagamentoMapper.toDomain(entity);
    }
}
