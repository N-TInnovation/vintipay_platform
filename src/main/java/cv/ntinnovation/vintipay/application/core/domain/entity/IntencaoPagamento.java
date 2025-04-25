package cv.ntinnovation.vintipay.application.core.domain.entity;

import cv.ntinnovation.vintipay.application.core.domain.enums.StatusIntencaoPagamentoEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntencaoPagamento {
    private Long id;
    private String referencia;
    private BigDecimal valor;
    private String moeda;
    private String descricao;
    private StatusIntencaoPagamentoEnum status;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private ContaPagamento contaPagamento;
    
    // Campos para integração com o provedor de pagamento
    private String merchantRef;
    private String merchantSession;
    private String posId;
    private String entityCode;
    private String transactionCode;
    private Long referenceNumber;
    private String timestamp;
    
    // Campos para criação de conta de pagamento
    private String contaPagamentoEmail;
    private String contaPagamentoBillAddrCity;
    private String contaPagamentoBillAddrCountry;
    private String contaPagamentoBillAddrLine1;
    private String contaPagamentoBillAddrPostCode;
}
