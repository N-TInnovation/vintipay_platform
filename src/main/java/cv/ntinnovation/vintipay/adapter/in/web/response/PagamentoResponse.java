package cv.ntinnovation.vintipay.adapter.in.web.response;

import cv.ntinnovation.vintipay.application.core.domain.enums.StatusPagamentoEnum;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PagamentoResponse {
    private Long id;
    private String reference;
    private String fingerprint;
    private String timestamp;
    private StatusPagamentoEnum status;
    private String mensagem;
    private String panMascarado;
    private LocalDateTime createdAt;
    private IntencaoPagamentoResponse intencaoPagamento;
}
