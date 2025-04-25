package cv.ntinnovation.vintipay.adapter.in.web.dto;

import cv.ntinnovation.vintipay.application.core.domain.enums.StatusLinkPagamentoEnum;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkPagamentoRequest {
    
    @NotNull(message = "ID do produto é obrigatório")
    private Long produtoId;
    
    private String titulo;
    
    private String descricao;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dataExpiracao;
    
    private Integer quantidadeUsos;
    
    private StatusLinkPagamentoEnum status;
    
    private String urlRetornoSucesso;
    
    private String urlRetornoCancelamento;
}
