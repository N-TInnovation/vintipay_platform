package cv.ntinnovation.vintipay.adapter.out.client.api.verifika;

import cv.ntinnovation.vintipay.adapter.out.client.api.verifika.request.CredencialsRequest;
import cv.ntinnovation.vintipay.adapter.out.client.api.verifika.response.CredencialsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="verifikaAuthClient", url = "${verifika.auth-server-url}")
public interface VerifikaAuthClient {

    @PostMapping(path = "/realms/${verifika.admin.realm}/protocol/openid-connect/token", consumes = "application/x-www-form-urlencoded")
    CredencialsResponse login(CredencialsRequest credencialsRequest);
}
