package cv.ntinnovation.vintipay.adapter.in.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para receber as informações do comprador no checkout
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompradorInfoRequest {
    
    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Por favor, forneça um email válido")
    private String email;

    @NotBlank(message = "O email é obrigatório")
    private String name;
    
    @NotBlank(message = "A cidade é obrigatória")
    private String billAddrCity;

    @NotBlank(message = "A cidade é obrigatória")
    private String billAddrState;
    
    @NotBlank(message = "O país é obrigatório")
    private String billAddrCountry;
    
    @NotBlank(message = "O endereço é obrigatório")
    private String billAddrLine1;

    private String billAddrLine2;
    
    @NotBlank(message = "O código postal é obrigatório")
    private String billAddrPostCode;
    
    @NotBlank(message = "O código do telefone é obrigatório")
    @Pattern(regexp = "^[0-9]{1,4}$", message = "Código do telefone inválido")
    private String phoneCode;
    
    @NotBlank(message = "O número de telefone é obrigatório")
    @Pattern(regexp = "^[0-9]{7,15}$", message = "Número de telefone inválido")
    private String phoneNumber;

}
