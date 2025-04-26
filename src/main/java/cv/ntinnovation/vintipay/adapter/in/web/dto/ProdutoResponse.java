package cv.ntinnovation.vintipay.adapter.in.web.dto;

import cv.ntinnovation.vintipay.application.core.domain.enums.CategoriaProdutoEnum;
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
public class ProdutoResponse {
    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private String imagem;
    private String codigoSku;
    private Integer estoque;
    private CategoriaProdutoEnum categoria;
    private boolean ativo;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private String moeda;
    private Long proprietarioId;
}
