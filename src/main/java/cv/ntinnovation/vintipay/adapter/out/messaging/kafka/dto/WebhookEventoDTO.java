package cv.ntinnovation.vintipay.adapter.out.messaging.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebhookEventoDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String webhookUrl;
    private String tipo;
    private Object payload;
    private String timestamp;
}
