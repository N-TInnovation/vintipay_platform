package cv.ntinnovation.vintipay.application.core.domain.rules;

import cv.ntinnovation.vintipay.adapter.in.web.dto.SispWebhookRequest;
import cv.ntinnovation.vintipay.application.core.domain.entity.Pagamento;

public interface RetornoFingerprint {
    void validarFingerPrint(FingerPrintService fingerPrintService);
    void setPosAutCode(String posAutCode);

    Pagamento getPagamento();
    void setPagamento(Pagamento pagamento);

    String getAuthorizationCode();
    void setAuthorizationCode(String authorizationCode);

    void setMessageType(String messageType);
    void setMerchantRespCP(String merchantRespCP);
    void setMerchantRespTid(String merchantRespTid);
    void setMerchantRespMerchantRef(String merchantRespMerchantRef);
    void setMerchantRespMerchantSession(String merchantRespMerchantSession);
    void setMerchantRespPurchaseAmount(String merchantRespPurchaseAmount);
    void setMerchantRespMessageID(String merchantRespMessageID);
    void setMerchantRespPan(String merchantRespPan);
    void setMerchantResp(String merchantResp);
    void setMerchantRespTimeStamp(String merchantRespTimeStamp);
    void setMerchantRespEntityCode(String merchantRespEntityCode);
    void setMerchantRespReferenceNumber(String merchantRespReferenceNumber);
    void setMerchantRespClientReceipt(String merchantRespClientReceipt);
    void setMerchantRespAdditionalErrorMessage(String merchantRespAdditionalErrorMessage);
    void setMerchantRespReloadCode(String merchantRespReloadCode);
    void setResultFingerPrint(String resultFingerPrint);

    void setMerchantRespErrorCode(String merchantRespErrorCode);
    void setMerchantRespErrorDetail(String merchantRespErrorDetail);
    void setMerchantRespErrorDescription(String merchantRespErrorDescription);

    void toBuilder( SispWebhookRequest sispWebhookRequest);
}
