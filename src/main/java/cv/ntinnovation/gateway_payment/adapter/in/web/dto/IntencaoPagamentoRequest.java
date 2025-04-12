package cv.ntinnovation.gateway_payment.adapter.in.web.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IntencaoPagamentoRequest {
    private BigDecimal amount;
    private String currency;
    private Long metodoPagamentoId;
    private Long contaPagamentoId;
    private String email;
    private String billAddrCity;
    private String billAddrCountry;
    private String billAddrLine1;
    private String billAddrPostCode;
    private String merchantRef;
    private String merchantSession;
    private String entityCode;
    private String transactionCode;
    private Long referenceNumber;
}
