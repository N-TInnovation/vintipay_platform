package cv.ntinnovation.gateway_payment.application.core.domain.rules;

import cv.ntinnovation.gateway_payment.application.core.domain.exception.FingerprintInvalidoException;
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

	@Override
	public void validarFingerPrint(FingerPrintService fingerPrintService) {
		var fingerPrintResult = fingerPrintService.validatorFingerPrintMessageTypeOutros(this);
		if (!fingerPrintResult.getResult()) {
			throw new FingerprintInvalidoException(this.resultFingerPrint);
		}
	}
}
