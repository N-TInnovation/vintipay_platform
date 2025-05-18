package cv.ntinnovation.vintipay.adapter.out.client.api.brevo;

import cv.ntinnovation.vintipay.adapter.out.client.api.brevo.dto.BrevoEmailRequest;
import cv.ntinnovation.vintipay.adapter.out.client.api.brevo.dto.BrevoEmailResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "BrevoApi", url = "${brevo.api.url}")
public interface Brevo {
    
    /**
     * Sends an email campaign using Brevo API
     * 
     * @param apiKey The Brevo API key
     * @param request The email campaign request data
     * @return Response containing the campaign ID and status
     */
    @PostMapping(value = "", consumes = "application/json")
    ResponseEntity<BrevoEmailResponse> sendEmailCampaign(
            @RequestHeader("api-key") String apiKey,
            @RequestBody BrevoEmailRequest request
    );
}
