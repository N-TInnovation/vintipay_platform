package cv.ntinnovation.gateway_payment.adapter.in.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cv.ntinnovation.gateway_payment.adapter.in.web.dto.SispCheckoutRequest;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.Pagamento;
import cv.ntinnovation.gateway_payment.application.port.in.CriaPagamentoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Controller
@RequestMapping("/pagamento")
@RequiredArgsConstructor
public class CheckoutPagamentoController {

    private final CriaPagamentoUseCase criaPagamentoUseCase;
    
    @Value("${sisp.url.3d}")
    private String submitFormUrl;
    
    @Value("${sisp.checkout.return.url}")
    private String returnUrl;

    @GetMapping("/checkout/{reference}")
    public String checkout(@PathVariable String reference, Model model) {
        var pagamento = criaPagamentoUseCase.criar(reference);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String purchaseRequest = objectMapper.writeValueAsString(pagamento.getIntencaoPagamento().getContaPagamento());
            purchaseRequest = Base64.getEncoder().encodeToString(purchaseRequest.getBytes());

            var referenceNumber = pagamento.getIntencaoPagamento().getReferenceNumber() == null ? null : pagamento.getIntencaoPagamento().getReferenceNumber().toString();
            var entityCode = pagamento.getIntencaoPagamento().getEntityCode() == null ? null : pagamento.getIntencaoPagamento().getEntityCode();

            var request = SispCheckoutRequest.builder()
                    .timeStamp(pagamento.getTimestamp())
                    .purchaseRequest(purchaseRequest)
                    .fingerprint(pagamento.getFingerprint())
                    .fingerprintversion("1") // Versão fixa por enquanto
                    .amount(pagamento.getIntencaoPagamento().getAmount().toString())
                    .transactionCode(pagamento.getIntencaoPagamento().getTransactionCode())
                    .posID(pagamento.getIntencaoPagamento().getPosId())
                    .merchantRef(pagamento.getIntencaoPagamento().getMerchantRef())
                    .merchantSession(pagamento.getIntencaoPagamento().getMerchantSession())
                    .currency(pagamento.getIntencaoPagamento().getCurrency())
                    .is3DSec("true") // Habilitado por padrão
                    .urlMerchantResponse(returnUrl)
                    .languageMessages("pt") // Português por padrão
                    .entityCode(entityCode)
                    .referenceNumber(referenceNumber)
                    .submitFormUrl(String.format("%s?FingerPrint=%s&TimeStamp=%s&FingerPrintVersion=%s",
                            submitFormUrl,
                            UriUtils.encode(pagamento.getFingerprint(), StandardCharsets.UTF_8),
                            UriUtils.encode(pagamento.getTimestamp(), StandardCharsets.UTF_8),
                            UriUtils.encode("1", StandardCharsets.UTF_8)))
                    .build();

            model.addAttribute("pagamento", pagamento);
            model.addAttribute("request", request);

            return "checkout";

        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
