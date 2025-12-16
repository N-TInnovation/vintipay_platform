package cv.ntinnovation.vintipay.adapter.out.client.api.verifika.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CredencialsRequest {
    @JsonProperty("grant_type")
    private String grant_type;
    @JsonProperty("client_id")
    private String client_id;
    @JsonProperty("client_secret")
    private String client_secret;
    private String username;
    private String password;
}
