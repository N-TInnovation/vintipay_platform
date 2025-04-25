package cv.ntinnovation.vintipay.adapter.in.web.dto;

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
public class LinkPagamentoResponse {
    private Long id;
    private String codigo;
    private String titulo;
    private String descricao;
    private StatusLinkPagamentoEnum status;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataExpiracao;
    private Integer quantidadeUsos;
    private Integer usos;
    private Long produtoId;
    private String produtoNome;
    private String produtoDescricao;
    private String produtoImagem;
    private String produtoPreco;
    private String produtoMoeda;
    private Long proprietarioId;
    private String urlRetornoSucesso;
    private String urlRetornoCancelamento;
    private String urlCheckout;
    private boolean disponivel;
}
