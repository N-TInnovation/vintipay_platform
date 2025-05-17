package cv.ntinnovation.vintipay.adapter.in.web.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class EnvironmentProperties {
    @Value("${sisp.url.prod.3d}")
    private String submitPaymentProdFormUrl;

    @Value("${sisp.url.dev.3d}")
    private String submitPaymentDevFormUrl;

    @Value("${sisp.url.qld.3d}")
    private String submitPaymentQldFormUrl;

    @Value("${sisp.checkout.return.url}")
    private String returnUrl;
}
