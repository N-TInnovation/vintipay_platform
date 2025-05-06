package cv.ntinnovation.vintipay.adapter.out.client.api.vintipay.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CredencialsRequest {
    private String clientId;
    private String clientSecret;
}
