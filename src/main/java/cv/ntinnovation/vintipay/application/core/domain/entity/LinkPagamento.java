package cv.ntinnovation.vintipay.application.core.domain.entity;

import cv.ntinnovation.vintipay.application.core.domain.enums.StatusLinkPagamentoEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkPagamento {
    private Long id;
    private String codigo; // código único para o link
    private String titulo; // título personalizado para o link (opcional)
    private String descricao; // descrição personalizada (opcional)
    private StatusLinkPagamentoEnum status;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataExpiracao; // opcional
    private Integer quantidadeUsos; // opcional, para limitar o número de usos
    private Integer usos; // contador de usos
    
    // Relacionamentos
    private Produto produto;
    private Long produtoId; // Para facilitar a criação
    private Usuario proprietario;
    private Long proprietarioId; // Para facilitar a criação
    private ConfiguracaoPagamento configuracaoPagamento;
    private Long configuracaoPagamentoId; // Para facilitar a criação
    private Long tenantId; // Tenant ao qual o link pertence
    
    // Campos adicionais
    private String urlRetornoSucesso; // URL para redirecionar após pagamento bem-sucedido
    private String urlRetornoCancelamento; // URL para redirecionar após cancelamento
    
    public boolean isAtivo() {
        return status == StatusLinkPagamentoEnum.ATIVO;
    }
    
    public boolean isExpirado() {
        if (dataExpiracao == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(dataExpiracao);
    }
    
    public boolean atingiuLimiteUsos() {
        if (quantidadeUsos == null || quantidadeUsos <= 0) {
            return false;
        }
        return usos >= quantidadeUsos;
    }
    
    public boolean isDisponivel() {
        return isAtivo() && !isExpirado() && !atingiuLimiteUsos();
    }
}
