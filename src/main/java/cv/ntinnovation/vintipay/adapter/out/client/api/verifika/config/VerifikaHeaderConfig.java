package cv.ntinnovation.vintipay.adapter.out.client.api.verifika.config;

import cv.ntinnovation.vintipay.adapter.out.client.api.verifika.VerifikaAuthClient;
import cv.ntinnovation.vintipay.adapter.out.client.api.verifika.request.CredencialsRequest;
import cv.ntinnovation.vintipay.adapter.out.client.api.verifika.response.CredencialsResponse;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@RequiredArgsConstructor
public class VerifikaHeaderConfig implements RequestInterceptor {

    private final VerifikaAuthClient verifikaAuthClient;
    @Value("${verifika.admin.username}")
    private String username;
    @Value("${verifika.admin.password}")
    private String password;
    @Value("${verifika.admin.clientId}")
    private String clientId;
    @Value("${verifika.admin.clientSecret}")
    private String clientSecret;
    @Value("${verifika.realm}")
    private String realm;

    @Override
    public void apply(RequestTemplate requestTemplate) {
        String path = requestTemplate.path();
        if(path.contains("/realms/" + realm + "/protocol/openid-connect/token")){
            return;
        }
        CredencialsRequest credencialsRequest = CredencialsRequest.builder()
                .grant_type("password")
                .client_id(clientId)
                .client_secret(clientSecret)
                .username(username)
                .password(password)
                .build();

        CredencialsResponse credencialsResponse = verifikaAuthClient.login(credencialsRequest);
        requestTemplate.header("Authorization", "Bearer " + credencialsResponse.getAccessToken()
        );
    }

}