package cv.ntinnovation.vintipay.adapter.out.persistence.entity;

import cv.ntinnovation.vintipay.application.core.domain.enums.CategoriaProdutoEnum;
import cv.ntinnovation.vintipay.application.core.domain.enums.PeriodoRecorrenciaEnum;
import cv.ntinnovation.vintipay.application.core.domain.enums.PrimeiraCobrancaEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    
    // Campos para cobranças recorrentes
    private boolean recorrente;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "periodo_recorrencia")
    private PeriodoRecorrenciaEnum periodoRecorrencia;
    
    @Column(name = "duracao_recorrencia")
    private Integer duracaoRecorrencia;
    
    @Column(name = "descricao_recorrencia")
    private String descricaoRecorrencia;
    
    // Novas configurações para cobranças recorrentes
    @Enumerated(EnumType.STRING)
    @Column(name = "primeira_cobranca")
    private PrimeiraCobrancaEnum primeiraCobranca;
    
    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DescontoCicloEntity> descontosPorCiclo = new ArrayList<>();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proprietario_id", nullable = false)
    private UsuarioEntity proprietario;
    
    // Helper method para adicionar desconto
    public void adicionarDescontoPorCiclo(DescontoCicloEntity desconto) {
        descontosPorCiclo.add(desconto);
        desconto.setProduto(this);
    }
    
    // Helper method para remover desconto
    public void removerDescontoPorCiclo(DescontoCicloEntity desconto) {
        descontosPorCiclo.remove(desconto);
        desconto.setProduto(null);
    }
}
