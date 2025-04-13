package cv.ntinnovation.gateway_payment.application.core.domain.entity;

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
    private String fingerprint;
    private String timestamp;
    private String status;
    private String mensagem;
    private String codigoAutorizacao;
    private String panMascarado;
    private LocalDateTime createdAt;

    public void gerarTimestamp() {
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM-dd HH:mm:ss"));
    }
}
