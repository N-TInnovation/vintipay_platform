package cv.ntinnovation.vintipay.adapter.in.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoRequest {
    
    @NotBlank(message = "Nome do produto é obrigatório")
    private String nome;
    
    private String descricao;
    
    @NotNull(message = "Preço do produto é obrigatório")
    @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
    private BigDecimal preco;
    
    private String imagem;
    
    private String codigoSku;
    
    private Integer estoque;
    
    private String categoria;
    
    private boolean ativo = true;
    
    private String moeda = "CVE"; // Moeda padrão para Cabo Verde
}
