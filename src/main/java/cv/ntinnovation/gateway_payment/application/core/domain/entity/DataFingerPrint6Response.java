package cv.ntinnovation.gateway_payment.application.core.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class DataFingerPrint6Response {

	private String posAutCode;
	private String messageType;
	private String merchantRespErrorCode;
	private String merchantRespErrorDetail;
	private String merchantRespErrorDescription;
	private String merchantRespMerchantRef;
	private String merchantRespMerchantSession;
	private String merchantRespAdditionalErrorMessage;
	private String resultFingerPrint;

}
