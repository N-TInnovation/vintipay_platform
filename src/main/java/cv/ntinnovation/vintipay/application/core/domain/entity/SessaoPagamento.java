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
public class SessaoPagamento {
    private Long id;
    private String sessionId;
    private String referencia;
    private BigDecimal valor;
    private String moeda;
    private String descricao;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataExpiracao;
    private String checkoutUrl;
    private String urlRetornoSucesso;
    private String urlRetornoCancelamento;
    private String intencaoPagamentoId;
    private String status;
    private String clientId;
    private String ipAddress;
    private String userAgent;
    private Boolean valid;
}
