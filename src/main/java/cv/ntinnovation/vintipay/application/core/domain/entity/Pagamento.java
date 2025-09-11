package cv.ntinnovation.vintipay.application.core.domain.entity;

import cv.ntinnovation.vintipay.application.core.domain.enums.AmbienteEnum;
import cv.ntinnovation.vintipay.application.core.domain.enums.StatusPagamentoEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pagamento {
    private Long id;
    private IntencaoPagamento intencaoPagamento;
    private String reference;
    private String fingerprint;
    private String timestamp;
    private StatusPagamentoEnum status;
    private String mensagem;
    private String panMascarado;
    private LocalDateTime createdAt;
    private String resultFingerPrint;
    private String authorizationCode;
    private String clearingPeriod;
    private String transactionId;
    private AmbienteEnum ambiente;

    public void gerarTimestamp() {
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
