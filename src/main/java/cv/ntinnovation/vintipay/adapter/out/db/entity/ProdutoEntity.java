package cv.ntinnovation.vintipay.adapter.out.db.entity;

import cv.ntinnovation.vintipay.application.core.domain.enums.CategoriaProdutoEnum;
import jakarta.persistence.*;
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
@Entity
@Table(name = "produtos")
public class ProdutoEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nome;
    
    @Column(columnDefinition = "TEXT")
    private String descricao;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;
    
    private String imagem;
    
    @Column(name = "codigo_sku")
    private String codigoSku;
    
    private Integer estoque;
    
    @Enumerated(EnumType.STRING)
    private CategoriaProdutoEnum categoria;
    
    private boolean ativo;
    
    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;
    
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;
    
    private String moeda;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proprietario_id", nullable = false)
    private UsuarioEntity proprietario;
}
