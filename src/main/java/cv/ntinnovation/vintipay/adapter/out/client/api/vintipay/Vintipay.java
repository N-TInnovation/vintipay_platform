package cv.ntinnovation.vintipay.adapter.out.client.api.vintipay;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name="vintipayapi", url = "${vintipay.url}")
public interface Vintipay {

    @PostMapping("/")
    public String estorno();
}
