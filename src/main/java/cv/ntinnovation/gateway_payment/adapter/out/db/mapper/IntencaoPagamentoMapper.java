package cv.ntinnovation.gateway_payment.adapter.out.db.mapper;

import cv.ntinnovation.gateway_payment.adapter.out.db.entity.ContaPagamentoEntity;
import cv.ntinnovation.gateway_payment.adapter.out.db.entity.InfoContaPagamentoEntity;
import cv.ntinnovation.gateway_payment.adapter.out.db.entity.IntencaoPagamentoEntity;
import cv.ntinnovation.gateway_payment.adapter.out.db.repository.ContaPagamentoRepository;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.ContaPagamento;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.InfoContaPagamento;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.IntencaoPagamento;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.MetodoPagamento;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IntencaoPagamentoMapper {

    private final ContaPagamentoRepository contaPagamentoRepository;
    private final MetodoPagamentoMapper metodoPagamentoMapper;

    public IntencaoPagamentoEntity toIntencaoPagamentoEntity(IntencaoPagamento intencaoPagamento) {
        if (intencaoPagamento == null) {
            return null;
        }

        IntencaoPagamentoEntity entity = new IntencaoPagamentoEntity();
        entity.setId(intencaoPagamento.getId());
        entity.setReference(intencaoPagamento.getReference());
        entity.setAmount(intencaoPagamento.getAmount());
        entity.setCurrency(intencaoPagamento.getCurrency());
        entity.setStatus(intencaoPagamento.getStatus());
        entity.setCreatedAt(intencaoPagamento.getCreatedAt());
        entity.setMerchantRef(intencaoPagamento.getMerchantRef());
        entity.setMerchantSession(intencaoPagamento.getMerchantSession());
        entity.setPosId(intencaoPagamento.getPosId());
        entity.setTimestamp(intencaoPagamento.getTimestamp());
        entity.setEntityCode(intencaoPagamento.getEntityCode());
        entity.setReferenceNumber(intencaoPagamento.getReferenceNumber());
        entity.setTransactionCode(intencaoPagamento.getTransactionCode());

        // Buscar a ContaPagamentoEntity existente pelo ID
        if (intencaoPagamento.getContaPagamento() != null && intencaoPagamento.getContaPagamento().getId() != null) {
            ContaPagamentoEntity contaPagamentoEntity = contaPagamentoRepository.getReferenceById(intencaoPagamento.getContaPagamento().getId());
            entity.setContaPagamento(contaPagamentoEntity);
        }

        entity.setMetodoPagamento(metodoPagamentoMapper.toMetodoPagamentoEntity(intencaoPagamento.getMetodoPagamento()));

        return entity;
    }

    public IntencaoPagamento toIntencaoPagamento(IntencaoPagamentoEntity entity) {
        if (entity == null) {
            return null;
        }

        ContaPagamento contaPagamento = toContaPagamento(entity.getContaPagamento());
        MetodoPagamento metodoPagamento = metodoPagamentoMapper.toMetodoPagamento(entity.getMetodoPagamento());

        return IntencaoPagamento.builder()
                .id(entity.getId())
                .reference(entity.getReference())
                .amount(entity.getAmount())
                .currency(entity.getCurrency())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .merchantRef(entity.getMerchantRef())
                .merchantSession(entity.getMerchantSession())
                .posId(entity.getPosId())
                .timestamp(entity.getTimestamp())
                .entityCode(entity.getEntityCode())
                .referenceNumber(entity.getReferenceNumber())
                .contaPagamento(contaPagamento)
                .metodoPagamento(metodoPagamento)
                .transactionCode(entity.getTransactionCode())
                .build();
    }

    private ContaPagamento toContaPagamento(ContaPagamentoEntity entity) {
        if (entity == null) {
            return null;
        }

        InfoContaPagamento infoContaPagamento = toInfoContaPagamento(entity.getAcctInfo());

        return ContaPagamento.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .addrMatch(entity.getAddrMatch())
                .billAddrCountry(entity.getBillAddrCountry())
                .billAddrCity(entity.getBillAddrCity())
                .billAddrLine1(entity.getBillAddrLine1())
                .billAddrLine2(entity.getBillAddrLine2())
                .billAddrLine3(entity.getBillAddrLine3())
                .billAddrPostCode(entity.getBillAddrPostCode())
                .billAddrState(entity.getBillAddrState())
                .shipAddrCountry(entity.getShipAddrCountry())
                .shipAddrCity(entity.getShipAddrCity())
                .shipAddrLine1(entity.getShipAddrLine1())
                .shipAddrPostCode(entity.getShipAddrPostCode())
                .shipAddrState(entity.getShipAddrState())
                .acctInfo(infoContaPagamento)
                .build();
    }

    private InfoContaPagamento toInfoContaPagamento(InfoContaPagamentoEntity entity) {
        if (entity == null) {
            return null;
        }
        return InfoContaPagamento.builder()
                .chAccAgeInd(entity.getChAccAgeInd())
                .chAccChange(entity.getChAccChange())
                .chAccDate(entity.getChAccDate())
                .chAccPwChange(entity.getChAccPwChange())
                .chAccPwChangeInd(entity.getChAccPwChangeInd())
                .suspiciousAccActivity(entity.getSuspiciousAccActivity())
                .build();
    }
}
