package cv.ntinnovation.vintipay.adapter.out.client.api.vintipay.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IntencaoPagamentoRequest {

    private BigDecimal amount;
    private String currency;
    private String email;
    private String billAddrCity;
    private String billAddrCountry;
    private String billAddrLine1;
    private String billAddrPostCode;
    private String phoneCode;
    private String phoneNumber;
    private String merchantRef;
    private String merchantSession;

    // Campos opcionais
    private String entityCode;
    private Long referenceNumber;
}
