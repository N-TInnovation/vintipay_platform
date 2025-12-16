package cv.ntinnovation.vintipay.adapter.out.client.api.verifika.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CredencialsResponse {
    @JsonProperty("token_type")
    private String tokenType;
    @JsonProperty("expires_in")
    private String expiresIn;
    @JsonProperty("refresh_token")
    private String refreshToken;
    @JsonProperty("refresh_expires_in")
    private String refreshExpiresIn;
    private String scope;
    @JsonProperty("access_token")
    private String accessToken;
    private String error;
    @JsonProperty("session_state")
    private String sessionState;
}
