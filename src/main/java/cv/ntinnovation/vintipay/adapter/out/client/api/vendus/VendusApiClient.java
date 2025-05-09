package cv.ntinnovation.vintipay.adapter.out.client.api.vendus;

import cv.ntinnovation.vintipay.adapter.out.client.api.vendus.dto.VendusReceiptRequest;
import cv.ntinnovation.vintipay.adapter.out.client.api.vendus.dto.VendusReceiptResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name="Vendusapi", url = "${vendus.api.base-url}")
public interface VendusApiClient {

    @PostMapping("/receipts")
    ResponseEntity<VendusReceiptResponse> criarRecibo(@RequestHeader("Authorization") String authorization, @RequestBody VendusReceiptRequest request);

    @GetMapping("/receipts/{receiptId}")
    ResponseEntity<VendusReceiptResponse> consultarRecibo(@RequestHeader("Authorization") String authorization, @PathVariable Long receiptId);
}
