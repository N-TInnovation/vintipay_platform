package cv.ntinnovation.vintipay.adapter.out.client.api.sisp;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name="Sispapi", url = "${sisp.url.}")
public interface Sisp {

    @PostMapping("/BizMPIOnUsSisp/CardPayment")
    public String estorno();
}
