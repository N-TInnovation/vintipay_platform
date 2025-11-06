package cv.ntinnovation.vintipay.adapter.out.client.api.vintipay;

import cv.ntinnovation.vintipay.adapter.out.client.api.vintipay.config.VintipayConfig;
import cv.ntinnovation.vintipay.adapter.out.client.api.vintipay.request.EventoRequest;
import cv.ntinnovation.vintipay.adapter.out.client.api.vintipay.request.IntencaoPagamentoRequest;
import cv.ntinnovation.vintipay.adapter.out.client.api.vintipay.response.IntencaoPagamentoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.net.URI;

@FeignClient(name="vintipayapi", url = "${app.base-url}" , configuration = VintipayConfig.class)
public interface Vintipay {

    @PostMapping("/api/v1/pagamentos/intencao/compras")
    public IntencaoPagamentoResponse criarIntencaoPagamento(@RequestHeader("Authorization") String token, @RequestBody IntencaoPagamentoRequest intencaoPagamentoRequest);

    @PostMapping
    public void enviaEventosWebhook(URI url, @RequestHeader("Authorization") String token, @RequestBody EventoRequest<?> eventoRequest);
}
