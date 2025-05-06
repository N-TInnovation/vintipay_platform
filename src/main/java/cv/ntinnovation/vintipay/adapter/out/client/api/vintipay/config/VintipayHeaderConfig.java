package cv.ntinnovation.vintipay.adapter.out.client.api.vintipay.config;

import cv.ntinnovation.vintipay.adapter.out.client.api.vintipay.VintipayAuth;
import cv.ntinnovation.vintipay.adapter.out.client.api.vintipay.request.CredencialsRequest;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class VintipayHeaderConfig implements RequestInterceptor {


    @Override
    public void apply(RequestTemplate requestTemplate) {

    }

}