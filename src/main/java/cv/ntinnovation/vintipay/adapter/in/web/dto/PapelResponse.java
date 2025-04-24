package cv.ntinnovation.vintipay.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PapelResponse {
    private Long id;
    private String nome;
    private String descricao;
    private String codigo;
    private boolean padrao;
    private Set<PermissaoResponse> permissoes = new HashSet<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
