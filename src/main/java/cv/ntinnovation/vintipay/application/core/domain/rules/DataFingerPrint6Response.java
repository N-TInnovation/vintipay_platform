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
    private String merchantRespCP;
    private String merchantRespTid;
	private String merchantRespMessageID;
    private String merchantRespPan;
	private String merchantRespErrorCode;
	private String merchantRespErrorDetail;
	private String merchantRespErrorDescription;
	private String merchantRespMerchantRef;
	private String merchantRespMerchantSession;
	private String merchantRespAdditionalErrorMessage;
	private String merchantRespTimeStamp;
	private String resultFingerPrint;
	private String authorizationCode;
    private String merchantResp;
    private String merchantRespEntityCode;
    private String merchantRespReferenceNumber;
    private String merchantRespClientReceipt;
    private String merchantRespReloadCode;
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
    public void toBuilder(SispWebhookRequest sispWebhookRequest) {
        log.info("toBuilder: {}", sispWebhookRequest);
        var messageType = sispWebhookRequest.getMessageType();
        this.setMessageType(messageType.getCode());
        this.setMerchantRespTid(sispWebhookRequest.getMerchantRespTid());
        this.setMerchantResp(sispWebhookRequest.getMerchantResp());
        this.setMerchantRespPurchaseAmount(sispWebhookRequest.getMerchantRespPurchaseAmount().toString());
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
