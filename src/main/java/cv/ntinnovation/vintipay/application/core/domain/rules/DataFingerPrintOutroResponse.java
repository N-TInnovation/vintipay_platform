package cv.ntinnovation.vintipay.application.core.domain.rules;

import cv.ntinnovation.vintipay.adapter.in.web.dto.SispWebhookRequest;
import cv.ntinnovation.vintipay.application.core.domain.entity.Pagamento;
import cv.ntinnovation.vintipay.application.core.domain.enums.MessageRespTypeEnum;
import cv.ntinnovation.vintipay.application.core.domain.exception.FingerprintInvalidoException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class DataFingerPrintOutroResponse implements RetornoFingerprint {

	private String posAutCode;
	private String messageType;
	private String merchantRespMerchantRef;
	private String merchantRespMerchantSession;
	private String resultFingerPrint;
	private String authorizationCode;
	private Pagamento pagamento;


	@Override
	public void validarFingerPrint(FingerPrintService fingerPrintService) {
		var fingerPrintResult = fingerPrintService.validatorFingerPrintMessageTypeOutros(this);
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
    public void setMerchantRespPurchaseAmount(String merchantRespPurchaseAmount) {

    }

    @Override
    public void setMerchantRespMessageID(String merchantRespMessageID) {

    }

    @Override
    public void setMerchantRespPan(String merchantRespPan) {

    }

    @Override
    public void setMerchantResp(String merchantResp) {

    }

    @Override
    public void setMerchantRespTimeStamp(String merchantRespTimeStamp) {

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
    public void setMerchantRespAdditionalErrorMessage(String merchantRespAdditionalErrorMessage) {

    }

    @Override
    public void setMerchantRespReloadCode(String merchantRespReloadCode) {

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
        this.setMessageType(MessageRespTypeEnum.FALHA_OUTROS.getCode());
        this.setMerchantRespMerchantRef(sispWebhookRequest.getMerchantRespMerchantRef());
        this.setMerchantRespMerchantSession(sispWebhookRequest.getMerchantRespMerchantSession());
        this.setResultFingerPrint(sispWebhookRequest.getResultFingerPrint());
    }
}
