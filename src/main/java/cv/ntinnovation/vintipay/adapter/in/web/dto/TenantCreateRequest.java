package cv.ntinnovation.vintipay.adapter.in.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantCreateRequest {

    @NotBlank(message = "Nome da organização é obrigatório")
    @Size(min = 3, max = 200, message = "Nome deve ter entre 3 e 200 caracteres")
    private String nome;

    @Size(max = 200, message = "Nome fantasia deve ter no máximo 200 caracteres")
    private String nomeFantasia;

    @Size(max = 50, message = "Documento deve ter no máximo 50 caracteres")
    private String documento;

    @Email(message = "Email inválido")
    @Size(max = 100, message = "Email deve ter no máximo 100 caracteres")
    private String email;

    @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres")
    private String telefone;

    @Size(max = 1000, message = "Endereço deve ter no máximo 1000 caracteres")
    private String endereco;

    @Size(max = 100, message = "Cidade deve ter no máximo 100 caracteres")
    private String cidade;

    @Size(max = 100, message = "País deve ter no máximo 100 caracteres")
    private String pais;

    @Size(max = 1000, message = "Observações deve ter no máximo 1000 caracteres")
    private String observacoes;
}
