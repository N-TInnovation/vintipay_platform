package cv.ntinnovation.gateway_payment.adapter.in.web.dto;

import jakarta.validation.constraints.NotNull;
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
    @NotNull
    private BigDecimal amount;
    
    @NotNull
    private String currency;
    
    @NotNull
    private Long metodoPagamentoId;
    
    private String email;
    private String billAddrCity;
    private String billAddrCountry;
    private String billAddrLine1;
    private String billAddrPostCode;
    
    @NotNull
    private String merchantRef;
    
    @NotNull
    private String merchantSession;
    
    // Campos opcionais
    private String entityCode;
    private Long referenceNumber;
}
