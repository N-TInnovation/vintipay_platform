package cv.ntinnovation.vintipay.adapter.out.client.api.vintipay;

import cv.ntinnovation.vintipay.adapter.out.client.api.vintipay.request.CredencialsRequest;
import cv.ntinnovation.vintipay.adapter.out.client.api.vintipay.request.IntencaoPagamentoRequest;
import cv.ntinnovation.vintipay.adapter.out.client.api.vintipay.response.CredencialsResponse;
import cv.ntinnovation.vintipay.adapter.out.client.api.vintipay.response.IntencaoPagamentoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name="vintipayauthapi", url = "${app.base-url}" )
public interface VintipayAuth {

    @PostMapping("/api/v1/authorize")
    public CredencialsResponse criarAutorizacao(CredencialsRequest credencialsRequest);
}
