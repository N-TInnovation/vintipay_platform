package cv.ntinnovation.vintipay.adapter.out.db.mapper;

import cv.ntinnovation.vintipay.adapter.out.db.entity.ContaPagamentoEntity;
import cv.ntinnovation.vintipay.adapter.out.db.entity.ContatoContaPagamentoEntity;
import cv.ntinnovation.vintipay.adapter.out.db.entity.InfoContaPagamentoEntity;
import cv.ntinnovation.vintipay.application.core.domain.entity.ContaPagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.ContatoContaPagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.InfoContaPagamento;
import org.springframework.stereotype.Component;

@Component
public class ContaPagamentoMapper {

    public ContaPagamento toContaPagamento(ContaPagamentoEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return ContaPagamento.builder()
                .id(entity.getId())
                .name(entity.getName())
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
                .acctInfo(toInfoContaPagamento(entity.getAcctInfo()))
                .mobilePhone(toContatoContaPagamento(entity.getMobilePhone()))
                .build();
    }

    public ContatoContaPagamento toContatoContaPagamento(ContatoContaPagamentoEntity entity) {
        if (entity == null) {
            return null;
        }
        return ContatoContaPagamento.builder()
                .cc(entity.getCc())
                .subscriber(entity.getSubscriber())
                .build();
    }

    public InfoContaPagamento toInfoContaPagamento(InfoContaPagamentoEntity entity) {
        if (entity == null) {
            return null;
        }

        return InfoContaPagamento.builder()
                .id(entity.getId())
                .chAccAgeInd(entity.getChAccAgeInd())
                .chAccChange(entity.getChAccChange())
                .chAccDate(entity.getChAccDate())
                .chAccPwChange(entity.getChAccPwChange())
                .chAccPwChangeInd(entity.getChAccPwChangeInd())
                .suspiciousAccActivity(entity.getSuspiciousAccActivity())
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
        entity.setId(domain.getId());
        entity.setName(domain.getName());
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
        entity.setAcctInfo(getInfoContaPagamentoEntity(domain));

        ContatoContaPagamentoEntity mobilePhone = new ContatoContaPagamentoEntity();
        mobilePhone.setCc(domain.getMobilePhone().getCc());
        mobilePhone.setSubscriber(domain.getMobilePhone().getSubscriber());
        entity.setMobilePhone(mobilePhone);

        return entity;
    }

    private InfoContaPagamentoEntity getInfoContaPagamentoEntity(ContaPagamento domain) {
        InfoContaPagamentoEntity infoContaPagamentoEntity = new InfoContaPagamentoEntity();
        infoContaPagamentoEntity.setChAccAgeInd(domain.getAcctInfo().getChAccAgeInd());
        infoContaPagamentoEntity.setChAccChange(domain.getAcctInfo().getChAccChange());
        infoContaPagamentoEntity.setChAccDate(domain.getAcctInfo().getChAccDate());
        infoContaPagamentoEntity.setChAccPwChange(domain.getAcctInfo().getChAccPwChange());
        infoContaPagamentoEntity.setChAccPwChangeInd(domain.getAcctInfo().getChAccPwChangeInd());
        infoContaPagamentoEntity.setSuspiciousAccActivity(domain.getAcctInfo().getSuspiciousAccActivity());
        return infoContaPagamentoEntity;
    }
}
