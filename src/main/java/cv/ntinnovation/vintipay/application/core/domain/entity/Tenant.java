package cv.ntinnovation.vintipay.application.core.domain.entity;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Tenant {
    private Long id;
    private String codigo;
    private String slug;
    private String nome;
    private String nomeFantasia;
    private String documento;
    private String email;
    private String telefone;
    private String endereco;
    private String cidade;
    private String pais;
    private Boolean ativo;
    private String logoUrl;
    private String configuracoes;
    private String observacoes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Builder.Default
    private List<Usuario> usuarios = new ArrayList<>();
    
    @Builder.Default
    private List<ConfiguracaoPagamento> configuracoesPagamento = new ArrayList<>();
    
    @Builder.Default
    private List<Produto> produtos = new ArrayList<>();
    
    @Builder.Default
    private List<LinkPagamento> linksPagamento = new ArrayList<>();
    
    @Builder.Default
    private List<Plano> planos = new ArrayList<>();
}
