package cv.ntinnovation.gateway_payment.adapter.in.web.mapper;

import cv.ntinnovation.gateway_payment.adapter.in.web.dto.CheckoutResponse;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.Pagamento;
import org.springframework.stereotype.Component;

@Component
public class CheckoutWebMapper {

    public CheckoutResponse toCheckoutResponse(Pagamento pagamento) {
        return CheckoutResponse.builder()
                .amount(pagamento.getTotal())
                .currency(pagamento.getCurrency())
                .fingerprint(pagamento.getFingerprint())
                .timestamp(pagamento.getTimestamp())
                .merchantRef(pagamento.getMerchantRef())
                .merchantSession(pagamento.getMerchantSession())
                .posId(pagamento.getPosId())
                .transactionCode(pagamento.getTransactionCode())
                .entityCode(pagamento.getEntityCode())
                .referenceNumber(pagamento.getReferenceNumber())
                .build();
    }
}
