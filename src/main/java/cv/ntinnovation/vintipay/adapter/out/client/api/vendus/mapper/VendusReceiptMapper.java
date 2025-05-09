package cv.ntinnovation.vintipay.adapter.out.client.api.vendus.mapper;

import cv.ntinnovation.vintipay.adapter.out.client.api.vendus.dto.VendusReceiptResponse;
import cv.ntinnovation.vintipay.application.core.domain.entity.VendusReceipt;
import org.springframework.stereotype.Component;

@Component
public class VendusReceiptMapper {
    public VendusReceipt toVendusReceipt(VendusReceiptResponse response) {
        return VendusReceipt.builder()
            .id(response.getId())
            .status(response.getStatus())
            .build();
    }
}
