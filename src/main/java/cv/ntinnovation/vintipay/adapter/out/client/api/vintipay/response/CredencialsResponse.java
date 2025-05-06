package cv.ntinnovation.vintipay.adapter.out.client.api.vintipay.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CredencialsResponse {
    private String accessToken;
    private String error;
}
