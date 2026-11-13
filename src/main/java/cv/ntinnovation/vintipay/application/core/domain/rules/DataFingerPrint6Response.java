package cv.ntinnovation.vintipay.application.core.domain.rules;

import cv.ntinnovation.vintipay.adapter.in.web.dto.SispWebhookRequest;
import cv.ntinnovation.vintipay.application.core.domain.entity.Pagamento;
import cv.ntinnovation.vintipay.application.core.domain.exception.FingerprintInvalidoException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
@AllArgsConstructor
@Data
public class DataFingerPrint6Response implements RetornoFingerprint {

    private String posAutCode;
    private String messageType;
    private String merchantRespMessageID;
    private String merchantRespErrorCode;
    private String merchantRespErrorDetail;
    private String merchantRespErrorDescription;
    private String merchantRespMerchantRef;
    private String merchantRespMerchantSession;
    private String merchantRespAdditionalErrorMessage;
    private String merchantRespTimeStamp;
    private String resultFingerPrint;
    private String authorizationCode;
    private String merchantRespPan;
    private String merchantResp;
    private String merchantRespPurchaseAmount;
    private Pagamento pagamento;

    @Override
    public void validarFingerPrint(FingerPrintService fingerPrintService) {
        var fingerPrintResult = fingerPrintService.validatorFingerPrintMessageType6(this);
        boolean invalidRef = !this.pagamento.getIntencaoPagamento().getMerchantRef().equals(merchantRespMerchantRef);
        boolean invalidSession = !this.pagamento.getIntencaoPagamento().getMerchantSession().equals(merchantRespMerchantSession);

        if(!fingerPrintResult.getResult() || invalidRef || invalidSession){
            throw new FingerprintInvalidoException(this.resultFingerPrint);
        }
    }

    @Override
    public void setMerchantRespCP(String merchantRespCP) {

    }

    @Override
    public void setMerchantRespTid(String merchantRespTid) {

    }


    @Override
    public void setMerchantRespEntityCode(String merchantRespEntityCode) {

    }

    @Override
    public void setMerchantRespReferenceNumber(String merchantRespReferenceNumber) {

    }

    @Override
    public void setMerchantRespClientReceipt(String merchantRespClientReceipt) {

    }

    @Override
    public void setMerchantRespReloadCode(String merchantRespReloadCode) {

    }

    @Override
    public void toBuilder(SispWebhookRequest sispWebhookRequest) {
        this.setMessageType(sispWebhookRequest.getMessageType().getCode());
        this.setMerchantRespTid(sispWebhookRequest.getMerchantRespTid());
        this.setMerchantResp(sispWebhookRequest.getMerchantResp());
        this.setMerchantRespPurchaseAmount(sispWebhookRequest.getMerchantRespPurchaseAmount() != null ? sispWebhookRequest.getMerchantRespPurchaseAmount().toString() : null);
        this.setMerchantRespPan(sispWebhookRequest.getMerchantRespPan());
        this.setMerchantRespErrorCode(sispWebhookRequest.getMerchantRespErrorCode());
        this.setMerchantRespErrorDetail(sispWebhookRequest.getMerchantRespErrorDetail());
        this.setMerchantRespErrorDescription(sispWebhookRequest.getMerchantRespErrorDescription());
        this.setMerchantRespMerchantRef(sispWebhookRequest.getMerchantRespMerchantRef());
        this.setMerchantRespMerchantSession(sispWebhookRequest.getMerchantRespMerchantSession());
        this.setMerchantRespAdditionalErrorMessage(sispWebhookRequest.getMerchantRespAdditionalErrorMessage());
        this.setResultFingerPrint(sispWebhookRequest.getResultFingerPrint());
        this.setMerchantRespMessageID(sispWebhookRequest.getMerchantRespMessageID());
        this.setMerchantRespTimeStamp(sispWebhookRequest.getMerchantRespTimeStamp());
    }
}
