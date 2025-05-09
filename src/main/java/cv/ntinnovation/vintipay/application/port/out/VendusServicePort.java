package cv.ntinnovation.vintipay.application.port.out;

import cv.ntinnovation.vintipay.adapter.out.client.api.vendus.dto.VendusReceiptResponse;
import cv.ntinnovation.vintipay.application.core.domain.entity.Pagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.VendusReceipt;

public interface VendusServicePort {
    VendusReceipt criarRecibo(String apiKey, Pagamento pagamento);
    VendusReceipt consultarRecibo(String apiKey,Long receiptId);
}
