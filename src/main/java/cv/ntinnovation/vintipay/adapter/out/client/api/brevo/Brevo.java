package cv.ntinnovation.vintipay.adapter.out.client.api.brevo;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name="Brevoapi", url = "${brevo.api.url}")
public interface Brevo {
    public void sendEmail(BrevoEmailRequest brevoEmailRequest);
}
