package cv.ntinnovation.vintipay.application.core.domain.entity;

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
public class Papel {
    private Long id;
    private String nome;
    private String descricao;
    private String codigo;
    private boolean padrao;
    private Set<Permissao> permissoes = new HashSet<>();
    private Set<Usuario> usuarios = new HashSet<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
