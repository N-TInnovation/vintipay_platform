package cv.ntinnovation.vintipay.adapter.out.persistence.mapper;

import cv.ntinnovation.vintipay.adapter.out.persistence.entity.*;
import cv.ntinnovation.vintipay.adapter.out.persistence.repository.ContaPagamentoRepository;
import cv.ntinnovation.vintipay.application.core.domain.entity.*;
import cv.ntinnovation.vintipay.application.core.domain.enums.AmbienteEnum;
import cv.ntinnovation.vintipay.application.core.domain.enums.StatusIntencaoPagamentoEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IntencaoPagamentoMapper {

    private final ContaPagamentoRepository contaPagamentoRepository;
    private final ProdutoMapper produtoMapper;


    public IntencaoPagamentoEntity toIntencaoPagamentoEntity(IntencaoPagamento intencaoPagamento) {
        if (intencaoPagamento == null) {
            return null;
        }

        IntencaoPagamentoEntity entity = new IntencaoPagamentoEntity();
        entity.setId(intencaoPagamento.getId());
        entity.setReference(intencaoPagamento.getReferencia());
        entity.setAmount(intencaoPagamento.getValor());
        entity.setCurrency(intencaoPagamento.getMoeda());
        entity.setStatus(intencaoPagamento.getStatus().toStatusPagamento());
        entity.setCreatedAt(intencaoPagamento.getDataCriacao());
        entity.setMerchantRef(intencaoPagamento.getMerchantRef());
        entity.setMerchantSession(intencaoPagamento.getMerchantSession());
        entity.setPosId(intencaoPagamento.getPosId());
        entity.setTimestamp(intencaoPagamento.getTimestamp());
        entity.setEntityCode(intencaoPagamento.getEntityCode());
        entity.setReferenceNumber(intencaoPagamento.getReferenceNumber());
        entity.setTransactionCode(intencaoPagamento.getTransactionCode());
        entity.setAmbiente(intencaoPagamento.getAmbiente().getAmbiente());
        entity.setProduto(produtoMapper.toEntity(intencaoPagamento.getProduto()));
        entity.setDataCobranca(intencaoPagamento.getDataCobranca());
        entity.setIntencao(intencaoPagamento.getIntencao());
        entity.setRecorrente(intencaoPagamento.getProduto().isRecorrente());
        entity.setCiclo(intencaoPagamento.getCiclo());

        if (intencaoPagamento.getTenant() != null) {
            TenantEntity tenantEntity = new TenantEntity();
            tenantEntity.setId(intencaoPagamento.getTenant().getId());
            entity.setTenant(tenantEntity);
        } else if (intencaoPagamento.getTenantId() != null) {
            TenantEntity tenantEntity = new TenantEntity();
            tenantEntity.setId(intencaoPagamento.getTenantId());
            entity.setTenant(tenantEntity);
        } else {
            throw new IllegalArgumentException("Tenant é obrigatório para criar uma intenção de pagamento");
        }

        // Buscar a ContaPagamentoEntity existente pelo ID
        if (intencaoPagamento.getContaPagamento() != null && intencaoPagamento.getContaPagamento().getId() != null) {
            ContaPagamentoEntity contaPagamentoEntity = contaPagamentoRepository.getReferenceById(intencaoPagamento.getContaPagamento().getId());
            contaPagamentoEntity.setName(intencaoPagamento.getContaPagamento().getName());
            entity.setContaPagamento(contaPagamentoEntity);
        }

        return entity;
    }

    public IntencaoPagamento toIntencaoPagamento(IntencaoPagamentoEntity entity) {
        if (entity == null) {
            return null;
        }

        ContaPagamento contaPagamento = toContaPagamento(entity.getContaPagamento());

        return IntencaoPagamento.builder()
                .id(entity.getId())
                .referencia(entity.getReference())
                .valor(entity.getAmount())
                .moeda(entity.getCurrency())
                .status(StatusIntencaoPagamentoEnum.fromStatusPagamento(entity.getStatus()))
                .dataCriacao(entity.getCreatedAt())
                .merchantRef(entity.getMerchantRef())
                .merchantSession(entity.getMerchantSession())
                .posId(entity.getPosId())
                .timestamp(entity.getTimestamp())
                .entityCode(entity.getEntityCode())
                .referenceNumber(entity.getReferenceNumber())
                .contaPagamento(contaPagamento)
                .transactionCode(entity.getTransactionCode())
                .ambiente(AmbienteEnum.fromAmbiente(entity.getAmbiente()))
                .dataCobranca(entity.getDataCobranca())
                .produto(produtoMapper.toDomain(entity.getProduto()))
                .intencao(entity.getIntencao())
                .ciclo(entity.getCiclo())
                .tenantId(entity.getTenant() != null ? entity.getTenant().getId() : null)
                .build();
    }



    private ContaPagamento toContaPagamento(ContaPagamentoEntity entity) {
        if (entity == null) {
            return null;
        }

        InfoContaPagamento infoContaPagamento = toInfoContaPagamento(entity.getAcctInfo());

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
                .acctInfo(infoContaPagamento)
                .mobilePhone(toContatoContaPagamento(entity.getMobilePhone()))
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

    private ContatoContaPagamento toContatoContaPagamento(ContatoContaPagamentoEntity entity) {
        if (entity == null) {
            return null;
        }
        return ContatoContaPagamento.builder()
                .cc(entity.getCc())
                .subscriber(entity.getSubscriber())
                .build();
    }
}
