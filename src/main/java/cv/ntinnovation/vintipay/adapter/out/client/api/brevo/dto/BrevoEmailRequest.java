package cv.ntinnovation.vintipay.adapter.out.client.api.brevo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BrevoEmailRequest {
    private String name;
    private String subject;
    private BrevoSender sender;
    private String type;
    private String htmlContent;
    private BrevoRecipients recipients;
    private String scheduledAt;
}
