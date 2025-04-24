package cv.ntinnovation.vintipay.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissaoResponse {
    private Long id;
    private String nome;
    private String descricao;
    private String codigo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
