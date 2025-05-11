package cv.ntinnovation.vintipay.adapter.in.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContaPagamentoRequest {

    @NotBlank(message = "Name é obrigatório")
    private String name;
    
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    private String email;
    
    @NotBlank(message = "Cidade é obrigatória")
    private String billAddrCity;
    
    @NotBlank(message = "País é obrigatório")
    private String billAddrCountry;
    
    private String billAddrLine1;

    private String billAddrLine2;
    
    private String billAddrPostCode;

    private String billAddrState;

    private String phoneCode;

    private String phoneNumber;
}
