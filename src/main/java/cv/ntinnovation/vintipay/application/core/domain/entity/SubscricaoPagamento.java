package cv.ntinnovation.vintipay.application.core.domain.entity;

import cv.ntinnovation.vintipay.application.core.domain.enums.AmbienteEnum;
import cv.ntinnovation.vintipay.application.core.domain.enums.StatusPagamentoEnum;
import cv.ntinnovation.vintipay.application.core.domain.enums.StatusSubscricaoPagamentoEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscricaoPagamento {
    private Long id;
    private IntencaoPagamento intencaoPagamento;
    private String reference;
    private String fingerprint;
    private StatusSubscricaoPagamentoEnum status;
    private String panMascarado;
    private String tokenPagamento;
    private LocalDateTime createdAt;
    private String resultFingerPrint;
    private ContaPagamento contaPagamento;
    private Integer ciclo;
    private AmbienteEnum ambiente;
}
