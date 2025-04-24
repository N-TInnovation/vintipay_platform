package cv.ntinnovation.vintipay.application.core.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Produto {
    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private String imagem;
    private String codigoSku;
    private boolean ativo;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    
    // Relacionamentos
    private Usuario proprietario;
    private Long proprietarioId; // Para facilitar a criação
    
    // Campos adicionais
    private Integer estoque; // Opcional, pode ser null para produtos digitais ou serviços
    private String categoria;
    private String moeda; // Por padrão, CVE
}
