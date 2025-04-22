package cv.ntinnovation.vintipay.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CredencialsRequest {
    @NotBlank
    private String clientId;
    @NotBlank
    private String clientSecret;
}
