package cv.ntinnovation.vintipay.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SispCheckoutRequest {
    private String timeStamp;
    private String fingerprint;
    private String fingerprintversion;
    private String amount;
    private String transactionCode;
    private String posID;
    private String merchantRef;
    private String merchantSession;
    private String currency;
    private String is3DSec;
    private String urlMerchantResponse;
    private String languageMessages;
    
    // Campos opcionais
    private String entityCode;
    private String referenceNumber;
    private String purchaseRequest;
    private String email;
    private String billAddrCity;
    private String billAddrCountry;
    private String billAddrLine1;
    private String billAddrPostCode;
    
    private String submitFormUrl;
}
