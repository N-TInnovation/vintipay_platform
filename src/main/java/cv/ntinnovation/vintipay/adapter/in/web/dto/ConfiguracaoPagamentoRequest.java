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
public class ConfiguracaoPagamentoRequest {
    
    private Long id;
    
    @NotBlank(message = "O POS ID é obrigatório")
    private String posId;

    private String merchantId;
    
    private String apiKey;
    
    private String apiSecret;

    @NotBlank(message = "O POS Aut Code é obrigatório")
    private String posAutCode;
    
    private String returnAutCode;

    private String returnUrl;

    private Boolean isActive;
    
    private String nomeLoja;
    
    private String urlLoja;
    
    private String telefone;
    
    @Email(message = "Formato de e-mail inválido")
    private String email;
    
    private Long usuarioId;
    
    private String ambiente;
    
    private String webhookUrl;
    
    private String webhookSecret;
    
    private Boolean ativo;
    
    private Boolean webhookAtivo;
    
    private Boolean returnAtivo;
    
    private String observacoes;
}
