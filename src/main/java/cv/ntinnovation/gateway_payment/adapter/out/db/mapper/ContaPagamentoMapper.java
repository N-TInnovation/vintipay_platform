package cv.ntinnovation.gateway_payment.adapter.out.db.mapper;

import cv.ntinnovation.gateway_payment.adapter.out.db.entity.ContaPagamentoEntity;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.ContaPagamento;
import org.springframework.stereotype.Component;

@Component
public class ContaPagamentoMapper {

    public ContaPagamento toContaPagamento(ContaPagamentoEntity entity) {
        if (entity == null) {
            return null;
        }
        
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
                .build();
    }
    
    // Alias method for consistency with other mappers
    public ContaPagamento toDomain(ContaPagamentoEntity entity) {
        return toContaPagamento(entity);
    }

    public ContaPagamentoEntity toContaPagamentoEntity(ContaPagamento domain) {
        if (domain == null) {
            return null;
        }
        
        ContaPagamentoEntity entity = new ContaPagamentoEntity();
        entity.setEmail(domain.getEmail());
        entity.setAddrMatch(domain.getAddrMatch());
        entity.setBillAddrCountry(domain.getBillAddrCountry());
        entity.setBillAddrCity(domain.getBillAddrCity());
        entity.setBillAddrLine1(domain.getBillAddrLine1());
        entity.setBillAddrLine2(domain.getBillAddrLine2());
        entity.setBillAddrLine3(domain.getBillAddrLine3());
        entity.setBillAddrPostCode(domain.getBillAddrPostCode());
        entity.setBillAddrState(domain.getBillAddrState());
        entity.setShipAddrCountry(domain.getShipAddrCountry());
        entity.setShipAddrCity(domain.getShipAddrCity());
        entity.setShipAddrLine1(domain.getShipAddrLine1());
        entity.setShipAddrPostCode(domain.getShipAddrPostCode());
        entity.setShipAddrState(domain.getShipAddrState());
        
        return entity;
    }
}
