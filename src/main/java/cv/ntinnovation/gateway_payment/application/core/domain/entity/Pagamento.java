package cv.ntinnovation.gateway_payment.application.core.domain.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Builder
public class Pagamento {
    private Long id;
    private IntencaoPagamento intencaoPagamento;
    private String fingerprint;
    private String timestamp;

    public void gerarTimestamp() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        this.timestamp = now.format(formatter);
    }
}
