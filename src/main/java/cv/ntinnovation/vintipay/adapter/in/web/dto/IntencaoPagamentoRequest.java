package cv.ntinnovation.vintipay.adapter.in.web.dto;

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

    @NotBlank
    private String email;

    @NotBlank
    private String name;

    @NotBlank
    private String billAddrCity;
    @NotBlank
    private String billAddrCountry;
    @NotBlank
    private String billAddrLine1;
    @NotBlank
    private String billAddrPostCode;
    private String billAddrState;
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
