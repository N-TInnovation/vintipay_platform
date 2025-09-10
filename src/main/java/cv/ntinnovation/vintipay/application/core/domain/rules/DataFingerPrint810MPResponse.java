package cv.ntinnovation.vintipay.application.core.domain.rules;

import cv.ntinnovation.vintipay.adapter.in.web.dto.SispWebhookRequest;
import cv.ntinnovation.vintipay.application.core.domain.entity.Pagamento;
import cv.ntinnovation.vintipay.application.core.domain.exception.FingerprintInvalidoException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class DataFingerPrint810MPResponse implements RetornoFingerprint {

	private String posAutCode;
	private String messageType;
	private String merchantRespCP;
	private String merchantRespTid;
	private String merchantRespMerchantRef;
	private String merchantRespMerchantSession;
	private String merchantRespPurchaseAmount;
	private String merchantRespMessageID;
	private String merchantRespPan;
	private String merchantResp;
	private String merchantRespTimeStamp;
	private String merchantRespEntityCode;
	private String merchantRespReferenceNumber;
	private String merchantRespClientReceipt;
	private String merchantRespAdditionalErrorMessage;
	private String merchantRespReloadCode;
	private String resultFingerPrint;
	private String authorizationCode;
	private Pagamento pagamento;


	@Override
	public void validarFingerPrint(FingerPrintService fingerPrintService) {
		var fingerPrintResult = fingerPrintService.validatorFingerPrintMessageType810MP(this);
        boolean invalidRef = !this.pagamento.getIntencaoPagamento().getMerchantRef().equals(merchantRespMerchantRef);
        boolean invalidSession = !this.pagamento.getIntencaoPagamento().getMerchantSession().equals(merchantRespMerchantSession);

        if(!fingerPrintResult.getResult() || invalidRef || invalidSession){
			throw new FingerprintInvalidoException(this.resultFingerPrint);
		}

	}

    @Override
    public void setMerchantRespErrorCode(String merchantRespErrorCode) {

    }

    @Override
    public void setMerchantRespErrorDetail(String merchantRespErrorDetail) {

    }

    @Override
    public void setMerchantRespErrorDescription(String merchantRespErrorDescription) {

    }

    @Override
    public void toBuilder(SispWebhookRequest sispWebhookRequest) {
        var messageType = sispWebhookRequest.getMessageType();
        this.setMessageType(messageType.getCode());
        this.setMerchantRespCP(sispWebhookRequest.getMerchantRespCP());
        this.setMerchantRespTid(sispWebhookRequest.getMerchantRespTid());
        this.setMerchantRespMerchantRef(sispWebhookRequest.getMerchantRespMerchantRef());
        this.setMerchantRespMerchantSession(sispWebhookRequest.getMerchantRespMerchantSession());
        this.setMerchantRespPurchaseAmount(sispWebhookRequest.getMerchantRespPurchaseAmount().toString());
        this.setMerchantRespMessageID(sispWebhookRequest.getMerchantRespMessageID());
        this.setMerchantRespPan(sispWebhookRequest.getMerchantRespPan());
        this.setMerchantResp(sispWebhookRequest.getMerchantResp());
        this.setMerchantRespTimeStamp(sispWebhookRequest.getMerchantRespTimeStamp());
        this.setMerchantRespEntityCode(sispWebhookRequest.getMerchantRespEntityCode());
        this.setMerchantRespReferenceNumber(sispWebhookRequest.getMerchantRespReferenceNumber());
        this.setMerchantRespClientReceipt(sispWebhookRequest.getMerchantRespClientReceipt());
        this.setMerchantRespAdditionalErrorMessage(sispWebhookRequest.getMerchantRespAdditionalErrorMessage());
        this.setMerchantRespReloadCode(sispWebhookRequest.getMerchantRespReloadCode());
        this.setResultFingerPrint(sispWebhookRequest.getResultFingerPrint());

    }
}
