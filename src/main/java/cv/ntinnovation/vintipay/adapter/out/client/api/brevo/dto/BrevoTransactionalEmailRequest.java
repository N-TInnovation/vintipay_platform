package cv.ntinnovation.vintipay.adapter.out.client.api.brevo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BrevoTransactionalEmailRequest {
    private BrevoSender sender;
    private List<BrevoRecipient> to;
    private List<BrevoRecipient> cc;
    private List<BrevoRecipient> bcc;
    private String subject;
    private String htmlContent;
    private String textContent;
    private Map<String, String> params;
}
