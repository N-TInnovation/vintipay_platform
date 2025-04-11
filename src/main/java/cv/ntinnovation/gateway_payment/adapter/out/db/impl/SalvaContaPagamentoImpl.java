package cv.ntinnovation.gateway_payment.adapter.out.db.impl;

import org.springframework.stereotype.Component;

import cv.ntinnovation.gateway_payment.adapter.out.db.entity.ContaPagamentoEntity;
import cv.ntinnovation.gateway_payment.adapter.out.db.entity.InfoContaPagamentoEntity;
import cv.ntinnovation.gateway_payment.adapter.out.db.mapper.ContaPagamentoMapper;
import cv.ntinnovation.gateway_payment.adapter.out.db.repository.ContaPagamentoRepository;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.ContaPagamento;
import cv.ntinnovation.gateway_payment.application.port.out.SalvaContaPagamento;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SalvaContaPagamentoImpl implements SalvaContaPagamento {

    private final ContaPagamentoRepository contaPagamentoRepository;
    private final ContaPagamentoMapper contaPagamentoMapper;

    @Override
    public ContaPagamento salvar(ContaPagamento contaPagamento) {
        try {
            ContaPagamentoEntity entity = contaPagamentoMapper.toContaPagamentoEntity(contaPagamento);
            
            // Configurar a InfoContaPagamento se existir
            if (contaPagamento.getAcctInfo() != null) {
                InfoContaPagamentoEntity infoEntity = new InfoContaPagamentoEntity();
                infoEntity.setAcctID(contaPagamento.getAcctInfo().getAcctID());
                infoEntity.setChAccAgeInd(contaPagamento.getAcctInfo().getChAccAgeInd());
                infoEntity.setChAccChange(contaPagamento.getAcctInfo().getChAccChange());
                infoEntity.setChAccDate(contaPagamento.getAcctInfo().getChAccDate());
                infoEntity.setChAccPwChange(contaPagamento.getAcctInfo().getChAccPwChange());
                infoEntity.setChAccPwChangeInd(contaPagamento.getAcctInfo().getChAccPwChangeInd());
                infoEntity.setSuspiciousAccActivity(contaPagamento.getAcctInfo().getSuspiciousAccActivity());
                entity.setAcctInfo(infoEntity);
            }
            
            ContaPagamentoEntity savedEntity = contaPagamentoRepository.save(entity);
            return contaPagamentoMapper.toContaPagamento(savedEntity);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao salvar conta de pagamento: " + e.getMessage(), e);
        }
    }
}
