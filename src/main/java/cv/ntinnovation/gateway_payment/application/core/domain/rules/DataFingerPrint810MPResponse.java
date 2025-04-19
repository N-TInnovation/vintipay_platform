package cv.ntinnovation.gateway_payment.application.core.domain.rules;

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


	@Override
	public void validarFingerPrint(FingerPrintService fingerPrintService) {
		var fingerPrintResult = fingerPrintService.validatorFingerPrintMessageType810MP(this);
		if(!fingerPrintResult.getResult()){
			throw new RuntimeException(fingerPrintResult.getHashFingerPrint());
		}

	}
}
