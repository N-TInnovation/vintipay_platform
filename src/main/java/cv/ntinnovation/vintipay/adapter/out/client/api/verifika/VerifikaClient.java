package cv.ntinnovation.vintipay.adapter.out.client.api.verifika;

import cv.ntinnovation.vintipay.adapter.out.client.api.verifika.config.VerifikaConfig;
import cv.ntinnovation.vintipay.adapter.out.client.api.verifika.request.UserRequest;
import cv.ntinnovation.vintipay.adapter.out.client.api.verifika.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name="verifikaapi", url = "${verifika.auth-server-url}", configuration = VerifikaConfig.class)
public interface VerifikaClient {

    @PostMapping(path = "/admin/realms/${verifika.realm}/users", consumes = "application/x-www-form-urlencoded")
    UserResponse createUser(@RequestBody UserRequest userRequest);

    @GetMapping(path = "/admin/realms/${verifika.realm}/users/{id}")
    UserResponse getUser(@PathVariable String id);

    @GetMapping(path = "/admin/realms/${verifika.realm}/users/{email}")
    UserResponse getUserByEmail(@PathVariable String email);

    @PutMapping(path = "/admin/realms/${verifika.realm}/users/{id}")
    UserResponse updateUser(@PathVariable String id, @RequestBody UserRequest userRequest);

    @DeleteMapping(path = "/admin/realms/${verifika.realm}/users/{id}")
    void deleteUser(@PathVariable String id);
}
