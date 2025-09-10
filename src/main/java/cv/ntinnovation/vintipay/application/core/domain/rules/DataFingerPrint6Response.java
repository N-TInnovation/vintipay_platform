package cv.ntinnovation.vintipay.application.core.domain.rules;

import cv.ntinnovation.vintipay.application.core.domain.entity.Pagamento;
import cv.ntinnovation.vintipay.application.core.domain.exception.FingerprintInvalidoException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

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
}
