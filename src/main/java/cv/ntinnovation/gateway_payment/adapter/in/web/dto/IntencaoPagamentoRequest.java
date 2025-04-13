package cv.ntinnovation.gateway_payment.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
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

    @NotBlank
    private String email;
    @NotBlank
    private String billAddrCity;
    @NotBlank
    private String billAddrCountry;
    @NotBlank
    private String billAddrLine1;
    @NotBlank
    private String billAddrPostCode;
    @NotBlank
    private String phoneCode;
    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String merchantRef;
    @NotBlank
    private String merchantSession;

    // Campos opcionais
    private String entityCode;
    private Long referenceNumber;
}
