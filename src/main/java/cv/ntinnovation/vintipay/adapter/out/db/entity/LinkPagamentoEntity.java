package cv.ntinnovation.vintipay.adapter.out.db.entity;

import cv.ntinnovation.vintipay.application.core.domain.enums.StatusLinkPagamentoEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "links_pagamento")
public class LinkPagamentoEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String codigo;
    
    private String titulo;
    
    @Column(columnDefinition = "TEXT")
    private String descricao;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusLinkPagamentoEnum status;
    
    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;
    
    @Column(name = "data_expiracao")
    private LocalDateTime dataExpiracao;
    
    @Column(name = "quantidade_usos")
    private Integer quantidadeUsos;
    
    private Integer usos;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)
    private ProdutoEntity produto;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proprietario_id", nullable = false)
    private UsuarioEntity proprietario;
    
    @Column(name = "url_retorno_sucesso")
    private String urlRetornoSucesso;
    
    @Column(name = "url_retorno_cancelamento")
    private String urlRetornoCancelamento;
}
